package org.apache.cxf.endpoint.jaxws;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;

import jakarta.xml.ws.Service;
import jakarta.xml.ws.soap.AddressingFeature.Responses;

import org.apache.commons.lang3.builder.Builder;
import org.apache.cxf.endpoint.jaxws.definition.SoapBound;
import org.apache.cxf.endpoint.jaxws.definition.SoapMethod;
import org.apache.cxf.endpoint.jaxws.definition.SoapParam;
import org.apache.cxf.endpoint.jaxws.definition.SoapResult;
import org.apache.cxf.endpoint.jaxws.definition.SoapService;
import org.apache.cxf.endpoint.utils.JaxwsEndpointApiUtils;

import io.github.easy4j.javassist.utils.ClassPoolFactory;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

/**
 * 
 * 动态构建ws接口
 * @see http://www.cnblogs.com/sunfie/p/5154246.html
 * @see http://blog.csdn.net/youaremoon/article/details/50766972
 * @see https://my.oschina.net/GameKing/blog/794580
 * @see http://wsmajunfeng.iteye.com/blog/1912983
 */
public class EndpointApiImplBuilder extends EndpointApiBuilder implements Builder<CtClass> {

    /** 
     * 生成的实现类名前缀 
     */  
    private static final String IMPL_CLASSNAME_PREFIX = "$Impl";  
	private EndpointApiInterfaceBuilder classBuilder;
	
	public EndpointApiImplBuilder(final String classname) throws CannotCompileException, NotFoundException  {
		this(ClassPoolFactory.getDefaultPool(), classname);
	}
  
	public EndpointApiImplBuilder(final ClassPool pool, final String classname) throws CannotCompileException, NotFoundException {
		
		super(pool, classname + "." + IMPL_CLASSNAME_PREFIX);
		
		this.classBuilder = new EndpointApiInterfaceBuilder(pool, classname);
		
	}
	
	/**
	 * 添加类注解 @WebService
	 */
	public EndpointApiImplBuilder webService(final SoapService service) {
		this.classBuilder.webService(service);
		return this;
	}
	
	/**
	 * 添加类注解 @ServiceMode
	 */
	public EndpointApiBuilder serviceMode(final Service.Mode mode) {
		
		this.classBuilder.serviceMode(mode);
        
		return this;
	}
	
	/**
	 * 添加类注解 @WebServiceProvider
	 */
	public EndpointApiBuilder webServiceProvider(String wsdlLocation, String serviceName,
			String targetNamespace, String portName) {

		this.classBuilder.webServiceProvider(wsdlLocation, serviceName, targetNamespace, portName);

		return this;
	}
	
	/**
	 * 添加类注解 @Addressing
	 */
	public EndpointApiBuilder annotAddressing(final boolean enabled, final boolean required,
			final Responses responses) {
		
		this.classBuilder.addressing(enabled, required, responses);
        
		return this;
	}
	
	/**
	 * 通过给动态类增加 <code>@WebBound</code>注解实现，数据的绑定
	 */
	public EndpointApiImplBuilder bind(final SoapBound bound) {
		this.classBuilder.bind(bound);
		return this;
	}
	
	/**
	 * 根据参数构造一个新的方法
	 * @param result ：返回结果信息
	 * @param method ：方法注释信息
	 * @param bound  ：方法绑定数据信息
	 * @param params ： 参数信息
	 * @return
	 * @throws CannotCompileException
	 * @throws NotFoundException 
	 */ 
	@Override
	public <T> EndpointApiImplBuilder newMethod(final SoapResult<T> result, final SoapMethod method, final SoapBound bound, SoapParam<?>... params) throws CannotCompileException, NotFoundException {
		this.classBuilder.abstractMethod(result, method, bound, params);
		CtClass returnType = result != null ? pool.get(result.getRtClass().getName()) : CtClass.voidType;
		CtMethod ctMethod = null;
		// 方法参数
		CtClass[] parameters = JaxwsEndpointApiUtils.makeParams(pool, params);
		// 有参方法
		if(parameters != null && parameters.length > 0) {
			ctMethod = new CtMethod(returnType, method.getOperationName(), parameters, declaring);
		} 
		// 无参方法 
		else {
			ctMethod = new CtMethod(returnType, method.getOperationName() , null, declaring);
		}
        // 设置方法体
        JaxwsEndpointApiUtils.methodBody(ctMethod, method);
        // 设置方法异常捕获逻辑
        JaxwsEndpointApiUtils.methodCatch(pool, ctMethod);
        
        //新增方法
        declaring.addMethod(ctMethod);
        
        return this;
	}
	
	@Override
	public CtClass build() {
		try {
			// 设置接口
			declaring.setSuperclass(classBuilder.build());
		} catch (CannotCompileException e) {
			e.printStackTrace();
		}
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
        	// 设置接口
   			declaring.setSuperclass(classBuilder.build());
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
        	// 设置接口
        	declaring.setSuperclass(classBuilder.build());
        	// 设置InvocationHandler参数构造器
			declaring.addConstructor(JaxwsEndpointApiUtils.makeConstructor(pool, declaring));
			// 通过类加载器加载该CtClass，并通过构造器初始化对象
			return declaring.toClass().getConstructor(InvocationHandler.class).newInstance(handler);
		} finally {
			// 将该class从ClassPool中删除
			declaring.detach();
		} 
	}

}