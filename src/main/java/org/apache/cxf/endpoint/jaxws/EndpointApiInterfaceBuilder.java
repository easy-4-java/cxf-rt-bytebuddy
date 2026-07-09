package org.apache.cxf.endpoint.jaxws;

import javax.xml.ws.Service;
import javax.xml.ws.soap.AddressingFeature.Responses;

import org.apache.commons.lang3.builder.Builder;
import org.apache.cxf.endpoint.jaxws.definition.SoapBound;
import org.apache.cxf.endpoint.jaxws.definition.SoapMethod;
import org.apache.cxf.endpoint.jaxws.definition.SoapParam;
import org.apache.cxf.endpoint.jaxws.definition.SoapResult;
import org.apache.cxf.endpoint.jaxws.definition.SoapService;
import org.apache.cxf.endpoint.utils.JaxwsEndpointApiUtils;

import io.github.hiwepy.javassist.utils.ClassPoolFactory;
import io.github.hiwepy.javassist.utils.JavassistUtils;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;

/**
 * 
 * 动态构建ws接口
 * <p>http://www.cnblogs.com/sunfie/p/5154246.html</p>
 * <p>http://blog.csdn.net/youaremoon/article/details/50766972</p>
 * <p>https://blog.csdn.net/tscyds/article/details/78415172</p>
 * <p>https://my.oschina.net/GameKing/blog/794580</p>
 * <p>http://wsmajunfeng.iteye.com/blog/1912983</p>
 */
public class EndpointApiInterfaceBuilder implements Builder<CtClass> {
	
	// 构建动态类
	private ClassPool pool = null;
	private CtClass declaring  = null;
	private ClassFile classFile = null;
	
	//private Loader loader = new Loader(pool);
	
	public EndpointApiInterfaceBuilder(final String classname) throws CannotCompileException, NotFoundException  {
		this(ClassPoolFactory.getDefaultPool(), classname);
	}
	
	public EndpointApiInterfaceBuilder(final ClassPool pool, final String classname) throws CannotCompileException, NotFoundException {
		
		this.pool = pool;
		this.declaring = JaxwsEndpointApiUtils.makeInterface(pool, classname);
		
		/* 指定 Cloneable 作为动态接口的父类 */
		CtClass superclass = pool.get(Cloneable.class.getName());
		declaring.setSuperclass(superclass);
		
		this.classFile = this.declaring.getClassFile();
	}
	
	/**
	 * 给动态类添加 @WebService 注解
	 * @param name： 此属性的值包含XML Web Service的名称。在默认情况下，该值是实现XML Web Service的类的名称，wsdl:portType 的名称。缺省值为 Java 类或接口的非限定名称。（字符串）
	 * @param targetNamespace：指定你想要的名称空间，默认是使用接口实现类的包名的反缀（字符串）
	 * @return
	 */
	public EndpointApiInterfaceBuilder webService(final String name, final String targetNamespace) {
		return this.webService(targetNamespace, targetNamespace, null, null, null, null);
	}
	
	public EndpointApiInterfaceBuilder webService(final String name, final String targetNamespace, String serviceName) {
		return this.webService(targetNamespace, targetNamespace, serviceName, null, null, null);
	}
	
	/**
	 * 给动态类添加 @WebService 注解
	 * @param name： 此属性的值包含XML Web Service的名称。在默认情况下，该值是实现XML Web Service的类的名称，wsdl:portType 的名称。缺省值为 Java 类或接口的非限定名称。（字符串）
	 * @param targetNamespace：指定你想要的名称空间，默认是使用接口实现类的包名的反缀（字符串）
	 * @param serviceName： 对外发布的服务名，指定 Web Service 的服务名称：wsdl:service。缺省值为 Java 类的简单名称 + Service。（字符串）
	 * @param portName：  wsdl:portName。缺省值为 WebService.name+Port。（字符串）
	 * @param wsdlLocation：指定用于定义 Web Service 的 WSDL 文档的 Web 地址。Web 地址可以是相对路径或绝对路径。（字符串）
	 * @param endpointInterface： 服务接口全路径, 指定做SEI（Service EndPoint Interface）服务端点接口（字符串）
	 * @return
	 */
	public EndpointApiInterfaceBuilder webService(final String name, final String targetNamespace, String serviceName,
			String portName, String wsdlLocation, String endpointInterface) {

		return webService(new SoapService(name, targetNamespace, serviceName, portName, wsdlLocation, endpointInterface));
	}
	
	/**
	 * 添加类注解 @WebService
	 */
	public EndpointApiInterfaceBuilder webService(final SoapService service) {

		ConstPool constPool = this.classFile.getConstPool();
		Annotation annot = JaxwsEndpointApiUtils.annotWebService(constPool, service);
		JavassistUtils.addClassAnnotation(declaring, annot);
		
		return this;
	}
	
	/**
	 * 添加类注解 @ServiceMode
	 */
	public EndpointApiInterfaceBuilder serviceMode(final Service.Mode mode) {
		
		ConstPool constPool = this.classFile.getConstPool();
		Annotation annot = JaxwsEndpointApiUtils.annotServiceMode(constPool, mode);
		JavassistUtils.addClassAnnotation(declaring, annot);
        
		return this;
	}
	
	/**
	 * 添加类注解 @WebServiceProvider
	 */
	public EndpointApiInterfaceBuilder webServiceProvider(String wsdlLocation, String serviceName,
			String targetNamespace, String portName) {

		ConstPool constPool = this.classFile.getConstPool();
		Annotation annot = JaxwsEndpointApiUtils.annotWebServiceProvider(constPool, wsdlLocation, serviceName,
				targetNamespace, portName);
		JavassistUtils.addClassAnnotation(declaring, annot);

		return this;
	}
	
	/**
	 * 添加类注解 @Addressing
	 */
	public EndpointApiInterfaceBuilder addressing(final boolean enabled, final boolean required,
			final Responses responses) {
		
		ConstPool constPool = this.classFile.getConstPool();
		Annotation annot = JaxwsEndpointApiUtils.annotAddressing(constPool, enabled, required, responses);
		JavassistUtils.addClassAnnotation(declaring, annot);
        
		return this;
	}
	
	/**
	 * 通过给动态类增加 <code>@WebBound</code>注解实现，数据的绑定
	 */
	public EndpointApiInterfaceBuilder bind(final String uid, final String json) {
		return bind(new SoapBound(uid, json));
	}
	
	/**
	 * 通过给动态类增加 <code>@WebBound</code>注解实现，数据的绑定
	 */
	public EndpointApiInterfaceBuilder bind(final SoapBound bound) {

		ConstPool constPool = this.classFile.getConstPool();
		Annotation annot = JaxwsEndpointApiUtils.annotWebBound(constPool, bound);
		JavassistUtils.addClassAnnotation(declaring, annot);
		
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
	public <T> EndpointApiInterfaceBuilder makeField(final String src) throws CannotCompileException {
		//创建属性
        declaring.addField(CtField.make(src, declaring));
		return this;
	}
	
	public <T> EndpointApiInterfaceBuilder newField(final Class<T> fieldClass, final String fieldName, final String fieldValue) throws CannotCompileException, NotFoundException {
		
		// 检查字段是否已经定义
		if(JavassistUtils.hasField(declaring, fieldName)) {
			return this;
		}
		
		/** 添加属性字段 */
		CtField field = new CtField(this.pool.get(fieldClass.getName()), fieldName, declaring);
        field.setModifiers(Modifier.PUBLIC);

        //新增Field
        declaring.addField(field, "\"" + fieldValue + "\"");
        
		return this;
	}
	
	public <T> EndpointApiInterfaceBuilder removeField(final String fieldName) throws NotFoundException {
		
		// 检查字段是否已经定义
		if(!JavassistUtils.hasField(declaring, fieldName)) {
			return this;
		}
		
		declaring.removeField(declaring.getDeclaredField(fieldName));
		
		return this;
	}
	
	/**
	 * 
	 * 根据参数构造一个新的方法
	 * @param rtClass 		：方法返回类型
	 * @param methodName 	：方法名称
	 * @param params		： 参数信息
	 * @return
	 * @throws CannotCompileException
	 * @throws NotFoundException 
	 */
	public <T> EndpointApiInterfaceBuilder abstractMethod(final Class<T> rtClass, final String methodName, SoapParam<?>... params) throws CannotCompileException, NotFoundException {
		return this.abstractMethod(new SoapResult<T>(rtClass), new SoapMethod(methodName), null, params);
	}
	
	/**
	 * 
	 * @author 		： <a href="https://github.com/hiwepy">hiwepy</a>
	 * @param rtClass 		：方法返回类型
	 * @param methodName 	：方法名称
	 * @param bound			：方法绑定数据信息
	 * @param params		： 参数信息
	 * @return
	 * @throws CannotCompileException
	 * @throws NotFoundException
	 */
	public <T> EndpointApiInterfaceBuilder abstractMethod(final Class<T> rtClass, final String methodName, final SoapBound bound, SoapParam<?>... params) throws CannotCompileException, NotFoundException {
		return this.abstractMethod(new SoapResult<T>(rtClass), new SoapMethod(methodName), bound, params);
	}
	
	/**
	 * 
	 * 根据参数构造一个新的方法
	 * @param result ：返回结果信息
	 * @param method ：方法注释信息
	 * @param bound  ：方法绑定数据信息
	 * @param params ： 参数信息
	 * @return
	 * @throws CannotCompileException
	 * @throws NotFoundException 
	 */ 
	public <T> EndpointApiInterfaceBuilder abstractMethod(final SoapResult<T> result, final SoapMethod method, final SoapBound bound, SoapParam<?>... params) throws CannotCompileException, NotFoundException {
	       
		ConstPool constPool = this.classFile.getConstPool();
		
		// 创建抽象方法
		CtClass returnType = result != null ? pool.get(result.getRtClass().getName()) : CtClass.voidType;
		CtClass[] exceptions = new CtClass[] { pool.get("java.lang.Exception") };
		// 方法参数
		CtClass[] parameters = JaxwsEndpointApiUtils.makeParams(pool, params);
		CtMethod ctMethod = null;
		// 有参方法
		if(parameters != null && parameters.length > 0) {
			ctMethod = CtNewMethod.abstractMethod(returnType, method.getOperationName(), parameters , exceptions, declaring);
		} 
		// 无参方法 
		else {
			ctMethod = CtNewMethod.abstractMethod(returnType, method.getOperationName(), null , exceptions, declaring);
		}
		
		// 为方法添加 @WebMethod、 @WebResult、@WebBound、@WebParam 注解
        JaxwsEndpointApiUtils.methodAnnotations(ctMethod, constPool, result, method, bound, params);
        
        //新增方法
        declaring.addMethod(ctMethod);
        
        return this;
	}
	
	public <T> EndpointApiInterfaceBuilder removeMethod(final String methodName, SoapParam<?>... params) throws NotFoundException {
		
		// 有参方法
		if(params != null && params.length > 0) {
			
			// 方法参数
			CtClass[] parameters = JaxwsEndpointApiUtils.makeParams(pool, params);
			
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
	 * @author 		： <a href="https://github.com/hiwepy">hiwepy</a>
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

}