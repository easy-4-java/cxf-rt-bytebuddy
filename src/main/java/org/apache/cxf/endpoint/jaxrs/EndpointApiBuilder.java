package org.apache.cxf.endpoint.jaxrs;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.Builder;
import org.apache.cxf.endpoint.EndpointApi;
import org.apache.cxf.endpoint.jaxrs.definition.HttpMethodEnum;
import org.apache.cxf.endpoint.jaxrs.definition.RestBound;
import org.apache.cxf.endpoint.jaxrs.definition.RestMethod;
import org.apache.cxf.endpoint.jaxrs.definition.RestParam;
import org.apache.cxf.endpoint.utils.JaxrsEndpointApiUtils;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import net.bytebuddy.utility.RandomString;

/**
 * 
 * 动态构建rs接口
 * <p>http://www.cnblogs.com/sunfie/p/5154246.html</p>
 * <p>http://blog.csdn.net/youaremoon/article/details/50766972</p>
 * <p>https://blog.csdn.net/tscyds/article/details/78415172</p>
 * <p>https://my.oschina.net/GameKing/blog/794580</p>
 * <p>http://wsmajunfeng.iteye.com/blog/1912983</p>
 */
public class EndpointApiBuilder<T extends EndpointApi> implements Builder<Unloaded<T>> {

	// 构建动态类
	protected net.bytebuddy.dynamic.DynamicType.Builder<? extends EndpointApi> builder = null;
	protected RandomString randomString = new RandomString(8);
	protected static final String PREFIX = "org.apache.cxf.endpoint.jaxrs.";

	public EndpointApiBuilder() {

		builder = new ByteBuddy().with(new NamingStrategy.AbstractBase() {
			@Override
			protected String name(TypeDescription typeDescription) {
				return PREFIX + typeDescription.getSimpleName() + "$" + randomString.nextString();
			}

		}).subclass(EndpointApi.class);

	}

	/**
	 * @param prefix
	 * @param randomName
	 */
	public EndpointApiBuilder(String prefix, boolean randomName) {

		builder = new ByteBuddy().with(new NamingStrategy.AbstractBase() {
			@Override
			protected String name(TypeDescription typeDescription) {
				return prefix + typeDescription.getSimpleName() + (randomName ? ("$" + randomString.nextString()) : "");
			}

		}).subclass(EndpointApi.class);

	}

	/**
	 * @param name The fully qualified name of the generated class in a binary format.
	 */
	public EndpointApiBuilder(String name) {
		builder = new ByteBuddy().subclass(EndpointApi.class).name(name);
	}

	/**
	 * 自定义命名策略
	 * @param namingStrategy ： The naming strategy to apply when creating a new auxiliary type.
	 */
	public EndpointApiBuilder(final NamingStrategy namingStrategy) {
		builder = new ByteBuddy().with(namingStrategy).subclass(EndpointApi.class);
	}
	
	/**
	 * 添加类注解 @Path
	 * @param path : Defines a URI template for the resource class or method, must not include matrix parameters.
	 * @return
	 */
	public EndpointApiBuilder<T> path(final String path) {

		builder.annotateType(JaxrsEndpointApiUtils.annotPath(constPool, path))
		
		
		return this;
	}
	
	/**
	 * 添加类注解 @Path
	 * @param mediaTypes
	 * @return
	 */
	public EndpointApiCtClassBuilder produces(final String... mediaTypes) {

		String[] noyNullMediaTypes = ArrayUtils.isNotEmpty(mediaTypes) ? mediaTypes : new String[] { "*/*" };
		ConstPool constPool = this.ccFile.getConstPool();
		JavassistUtils.addClassAnnotation(declaring, JaxrsEndpointApiUtils.annotProduces(constPool, noyNullMediaTypes));
		
		return this;
	}
	
	/**
	 * 通过给动态类增加 <code>@WebBound</code>注解实现，数据的绑定
	 */
	public EndpointApiCtClassBuilder bind(final String uid, final String json) {
		return bind(new RestBound(uid, json));
	}
	
	/**
	 * 通过给动态类增加 <code>@WebBound</code>注解实现，数据的绑定
	 */
	public EndpointApiCtClassBuilder bind(final RestBound bound) {

		ConstPool constPool = this.ccFile.getConstPool();
		JavassistUtils.addClassAnnotation(declaring, JaxrsEndpointApiUtils.annotWebBound(constPool, bound));
		
		return this;
	}
	
	/**
     * Compiles the given source code and creates a field.
     * Examples of the source code are:
     * 
     * <pre>
     * "public String name;"
     * "public int k = 3;"</pre>
     *
     * <p>Note that the source code ends with <code>';'</code>
     * (semicolon).
     *
     * @param src               the source text.
     */
	public <T> EndpointApiCtClassBuilder makeField(final String src) throws CannotCompileException {
		//创建属性
        declaring.addField(CtField.make(src, declaring));
		return this;
	}
	
	public <T> EndpointApiCtClassBuilder newField(final Class<T> fieldClass, final String fieldName, final String fieldValue) throws CannotCompileException, NotFoundException {
		
		// 检查字段是否已经定义
		if(JavassistUtils.hasField(declaring, fieldName)) {
			return this;
		}
		
		/** 添加属性字段 */
		CtField field = new CtField(this.pool.get(fieldClass.getName()), fieldName, declaring);
        field.setModifiers(Modifier.PROTECTED);

        //新增Field
        declaring.addField(field, "\"" + fieldValue + "\"");
        
		return this;
	}
	
	public <T> EndpointApiCtClassBuilder removeField(final String fieldName) throws NotFoundException {
		
		// 检查字段是否已经定义
		if(!JavassistUtils.hasField(declaring, fieldName)) {
			return this;
		}
		
		declaring.removeField(declaring.getDeclaredField(fieldName));
		
		return this;
	}
	
	public <T> EndpointApiCtClassBuilder newMethod(final Class<T> rtClass, final HttpMethodEnum method, final String name,final String path, final RestBound bound, RestParam<?>... params) throws CannotCompileException, NotFoundException {
		return this.newMethod(rtClass , new RestMethod(method, name, path), bound, params);
	}
	
	public <T> EndpointApiCtClassBuilder newMethod(final Class<T> rtClass, final HttpMethodEnum method, final String name,final String path, RestParam<?>... params) throws CannotCompileException, NotFoundException {
		return this.newMethod(rtClass , new RestMethod(method, name, path), params);
	}
	
	/**
	 * 
	 * 根据参数构造一个新的方法
	 * @param rtClass ：返回对象类型
	 * @param method ：方法注释信息
	 * @param bound  ：方法绑定数据信息
	 * @param params ： 参数信息
	 * @return
	 * @throws CannotCompileException
	 * @throws NotFoundException 
	 */ 
	public <T> EndpointApiCtClassBuilder newMethod(final Class<T> rtClass, final RestMethod method, final RestBound bound, RestParam<?>... params) throws CannotCompileException, NotFoundException {
	       
		ConstPool constPool = this.ccFile.getConstPool();
		
		// 创建抽象方法
		CtClass returnType = rtClass != null ? pool.get(rtClass.getName()) : CtClass.voidType;
		CtMethod ctMethod = null;
		// 方法参数
		CtClass[] parameters = JaxrsEndpointApiUtils.makeParams(pool, params);
		// 有参方法
		if(parameters != null && parameters.length > 0) {
			ctMethod = new CtMethod(returnType, method.getName(), parameters, declaring);
		} 
		// 无参方法 
		else {
			ctMethod = new CtMethod(returnType, method.getName() , null, declaring);
		}
        // 设置方法体
        JaxrsEndpointApiUtils.methodBody(ctMethod, method);
        // 设置方法异常捕获逻辑
        JaxrsEndpointApiUtils.methodCatch(pool, ctMethod);
        // 为方法添加 @HttpMethod、 @GET、 @POST、 @PUT、 @DELETE、 @PATCH、 @HEAD、 @OPTIONS、@Path、、@Consumes、@Produces、@RestBound、@RestParam 注解
        JaxrsEndpointApiUtils.methodAnnotations(ctMethod, constPool, method, bound, params);
        
        //新增方法
        declaring.addMethod(ctMethod);
        
        return this;
	}
	
	public <T> EndpointApiCtClassBuilder newMethod(final Class<T> rtClass, final RestMethod method, RestParam<?>... params) throws CannotCompileException, NotFoundException {
		return this.newMethod(rtClass, method, null, params);
	}
	
	public <T> EndpointApiCtClassBuilder newMethod(final HttpMethodEnum method, final String name, final String path, RestParam<?>... params) throws CannotCompileException, NotFoundException {
		return this.newMethod(null , new RestMethod(method, name, path), null, params);
	}
	
	public <T> EndpointApiCtClassBuilder newMethod(final HttpMethodEnum method, final String name, final String path, final RestBound bound, RestParam<?>... params) throws CannotCompileException, NotFoundException {
		return this.newMethod(null , new RestMethod(method, name, path), bound, params);
	}
	
	public <T> EndpointApiCtClassBuilder newMethod(final RestMethod method, final RestBound bound, RestParam<?>... params) throws CannotCompileException, NotFoundException {
		return this.newMethod(null, method, bound, params);
	}
	
	public <T> EndpointApiCtClassBuilder newMethod(final RestMethod method, RestParam<?>... params) throws CannotCompileException, NotFoundException {
		return this.newMethod(null, method, null, params);
	}
	
	public <T> EndpointApiCtClassBuilder removeMethod(final String methodName, RestParam<?>... params) throws NotFoundException {
		
		// 有参方法
		if(params != null && params.length > 0) {
			
			// 方法参数
			CtClass[] parameters = JaxrsEndpointApiUtils.makeParams(pool, params);
			
			// 检查方法是否已经定义
			if(!JavassistUtils.hasMethod(declaring, methodName, parameters)) {
				return this;
			}
			
			declaring.removeMethod(declaring.getDeclaredMethod(methodName, parameters));
			
		}
		else {
			
			// 检查方法是否已经定义
			if(!JavassistUtils.hasMethod(declaring, methodName)) {
				return this;
			}
			
			declaring.removeMethod(declaring.getDeclaredMethod(methodName));
			
		}
		
		return this;
	}
	
	@Override
	public CtClass build() {
        return declaring;
	}
	
	/**
	 * 
	 * javassist在加载类时会用Hashtable将类信息缓存到内存中，这样随着类的加载，内存会越来越大，甚至导致内存溢出。如果应用中要加载的类比较多，建议在使用完CtClass之后删除缓存
	 * @author 		： <a href="https://github.com/easy-4-java">hiwepy</a>
	 * @return
	 * @throws CannotCompileException
	 */
	public Class<?> toClass() throws CannotCompileException {
        try {
        	// 通过类加载器加载该CtClass
			return declaring.toClass();
		} finally {
			// 将该class从ClassPool中删除
			declaring.detach();
		} 
	}
	
	@SuppressWarnings("unchecked")
	public Object toInstance(final InvocationHandler handler) throws CannotCompileException, NotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        try {
        	// 设置InvocationHandler参数构造器
			declaring.addConstructor(JaxrsEndpointApiUtils.makeConstructor(pool, declaring));
			// 通过类加载器加载该CtClass，并通过构造器初始化对象
			return declaring.toClass().getConstructor(InvocationHandler.class).newInstance(handler);
		} finally {
			// 将该class从ClassPool中删除
			declaring.detach();
		} 
	}

}