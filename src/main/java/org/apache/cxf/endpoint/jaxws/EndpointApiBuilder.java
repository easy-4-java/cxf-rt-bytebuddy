package org.apache.cxf.endpoint.jaxws;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Service;
import javax.xml.ws.Service.Mode;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.soap.Addressing;
import javax.xml.ws.soap.AddressingFeature.Responses;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.endpoint.EndpointApi;
import org.apache.cxf.endpoint.agent.TimingInterceptor;
import org.apache.cxf.endpoint.annotation.WebBound;
import org.apache.cxf.endpoint.jaxws.definition.SoapBound;
import org.apache.cxf.endpoint.jaxws.definition.SoapMethod;
import org.apache.cxf.endpoint.jaxws.definition.SoapParam;
import org.apache.cxf.endpoint.jaxws.definition.SoapResult;
import org.apache.cxf.endpoint.jaxws.definition.SoapService;
import org.apache.cxf.endpoint.utils.JaxwsEndpointApiUtils;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType.Builder.MethodDefinition.ParameterDefinition.Initial;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.RandomString;

/**
 * 
 * 动态构建ws接口
 * 
 * @see http://bytebuddy.net/#/tutorial
 * @see https://notes.diguage.com/byte-buddy-tutorial/
 * @see https://www.jianshu.com/p/f55bfa7d472c
 * @see https://blog.csdn.net/qq_26761587/article/details/78798194
 */
public class EndpointApiBuilder<T extends EndpointApi> implements org.apache.commons.lang3.builder.Builder<Unloaded<T>> {

	// 构建动态类
	protected net.bytebuddy.dynamic.DynamicType.Builder<? extends EndpointApi> builder = null;
	protected RandomString randomString = new RandomString(8);
	protected static final String PREFIX = "org.apache.cxf.endpoint.jaxws.";

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
	 * 添加 @WebService 注解
	 * @param name： 此属性的值包含XML Web Service的名称。在默认情况下，该值是实现XML Web
	 *            Service的类的名称，wsdl:portType 的名称。缺省值为 Java 类或接口的非限定名称。（字符串）
	 * @param targetNamespace：指定你想要的名称空间，默认是使用接口实现类的包名的反缀（字符串）
	 * @return
	 */
	public EndpointApiBuilder<T> webService(final String name, final String targetNamespace) {
		this.webService(targetNamespace, targetNamespace, null, null, null, null);
		return this;
	}

	public EndpointApiBuilder webService(final String name, final String targetNamespace, String serviceName) {
		return this.webService(targetNamespace, targetNamespace, serviceName, null, null, null);
	}

	/**
	 * @description ： 给动态类添加 @WebService 注解
	 * @param name：
	 *            此属性的值包含XML Web Service的名称。在默认情况下，该值是实现XML Web
	 *            Service的类的名称，wsdl:portType 的名称。缺省值为 Java 类或接口的非限定名称。（字符串）
	 * @param targetNamespace：指定你想要的名称空间，默认是使用接口实现类的包名的反缀（字符串）
	 * @param serviceName：
	 *            对外发布的服务名，指定 Web Service 的服务名称：wsdl:service。缺省值为 Java 类的简单名称 +
	 *            Service。（字符串）
	 * @param portName：
	 *            wsdl:portName。缺省值为 WebService.name+Port。（字符串）
	 * @param wsdlLocation：指定用于定义
	 *            Web Service 的 WSDL 文档的 Web 地址。Web 地址可以是相对路径或绝对路径。（字符串）
	 * @param endpointInterface：
	 *            服务接口全路径, 指定做SEI（Service EndPoint Interface）服务端点接口（字符串）
	 * @return
	 */
	public EndpointApiBuilder<T> webService(final String name, final String targetNamespace, String serviceName,
			String portName, String wsdlLocation, String endpointInterface) {

		builder = JaxwsEndpointApiUtils.annotWebService(builder, name, targetNamespace, serviceName, portName,
				wsdlLocation, endpointInterface);

		return this;

	}

	/**
	 * 添加类注解 @WebService
	 */
	public EndpointApiBuilder<T> webService(final SoapService service) {

		return webService(service.getName(), service.getTargetNamespace(), service.getServiceName(),
				service.getPortName(), service.getWsdlLocation(), service.getEndpointInterface());

	}

	/**
	 * 添加类注解 @WebServiceProvider
	 * 
	 * @param wsdlLocation
	 *            ：Location of the WSDL description for the service.
	 * @param serviceName
	 *            ：Service name.
	 * @param targetNamespace
	 *            ：Target namespace for the service
	 * @param portName
	 *            ：Port name.
	 * @return
	 */
	public EndpointApiBuilder<T> webServiceProvider(final String wsdlLocation, final String serviceName,
			final String targetNamespace, final String portName) {

		builder = JaxwsEndpointApiUtils.annotWebServiceProvider(builder, wsdlLocation, serviceName, targetNamespace,
				portName);

		return this;
	}

	/**
	 * 添加类注解 @Addressing
	 */
	public EndpointApiBuilder<T> addressing(final boolean enabled, final boolean required,
			final Responses responses) {

		builder = JaxwsEndpointApiUtils.annotAddressing(builder, enabled, required, responses);

		return this;
	}

	/**
	 * 添加类注解 @ServiceMode
	 */
	public EndpointApiBuilder<T> serviceMode(final Service.Mode mode) {

		builder = JaxwsEndpointApiUtils.annotServiceMode(builder, mode);

		return this;
	}

	/**
	 * 通过给动态类增加 <code>@WebBound</code>注解实现，数据的绑定
	 */
	public EndpointApiBuilder<T> bind(final String uid, final String json) {
		return bind(new SoapBound(uid, json));
	}

	/**
	 * 通过给动态类增加 <code>@WebBound</code>注解实现，数据的绑定
	 */
	public EndpointApiBuilder<T> bind(final SoapBound bound) {

		builder = builder.annotateType(new WebBound() {

			@Override
			public Class<? extends java.lang.annotation.Annotation> annotationType() {
				return WebBound.class;
			}

			@Override
			public String uid() {
				return bound.getUid();
			}

			@Override
			public String json() {
				return bound.getJson();
			}

		});

		return this;
	}

	public <T> EndpointApiBuilder newField(final Class<T> fieldClass, final String fieldName,
			final String fieldValue) {
		builder = builder.defineField(fieldName, fieldClass, Modifier.PROTECTED).value(fieldValue);
		return this;
	}

	/**
	 * 
	 * 根据参数构造一个新的方法
	 * 
	 * @param rtClass
	 *            ：方法返回类型
	 * @param methodName
	 *            ：方法名称
	 * @param params
	 *            ： 参数信息
	 * @return
	 */
	public <T> EndpointApiBuilder newMethod(final Class<T> rtClass, final String methodName,
			SoapParam<?>... params) {
		return this.newMethod(new SoapResult<T>(rtClass), new SoapMethod(methodName), null, params);
	}

	/**
	 * 
	 * @author ： <a href="https://github.com/easy-4-java">hiwepy</a>
	 * @param rtClass
	 *            ：方法返回类型
	 * @param methodName
	 *            ：方法名称
	 * @param bound
	 *            ：方法绑定数据信息
	 * @param params
	 *            ： 参数信息
	 * @return
	 */
	public <T> EndpointApiBuilder newMethod(final Class<T> rtClass, final String methodName,
			final SoapBound bound, SoapParam<?>... params) {
		return this.newMethod(new SoapResult<T>(rtClass), new SoapMethod(methodName), bound, params);
	}

	/**
	 * 
	 * 根据参数构造一个新的方法
	 * 
	 * @param result
	 *            ：返回结果信息
	 * @param method
	 *            ：方法注释信息
	 * @param bound
	 *            ：方法绑定数据信息
	 * @param params
	 *            ： 参数信息
	 * @return
	 */
	public <T> EndpointApiBuilder newMethod(final SoapResult<T> result, final SoapMethod method,
			final SoapBound bound, SoapParam<?>... params) {

		Initial<? extends Object> initial = builder.defineMethod(method.getOperationName(), result.getRtClass());
		// 有参方法
		if (params != null && params.length > 0) {
			for (int i = 0; i < params.length; i++) {
				initial.withParameter(params[i].getType(), params[i].getName(), Modifier.PUBLIC);
			}
		}

		initial.throwing(Exception.class);

		/*
		 * // 设置方法体 JaxwsEndpointApiUtils.methodBody(ctMethod, method); // 设置方法异常捕获逻辑
		 * JaxwsEndpointApiUtils.methodCatch(pool, ctMethod); //
		 * 为方法添加 @WebMethod、 @WebResult、@WebBound、@WebParam 注解
		 * JaxwsEndpointApiUtils.methodAnnotations(ctMethod, constPool, result, method,
		 * bound, params);
		 * 
		 */
		return this;
	}

	@Override
	public Unloaded<? extends Object> build() {
		return builder.make();
	}

	/**
	 * 
	 * javassist在加载类时会用Hashtable将类信息缓存到内存中，这样随着类的加载，内存会越来越大，甚至导致内存溢出。如果应用中要加载的类比较多，建议在使用完CtClass之后删除缓存
	 * 
	 * @author ： <a href="https://github.com/easy-4-java">hiwepy</a>
	 * @return
	 * @throws CannotCompileException
	 */
	public Class<?> toClass() {
		// 通过类加载器加载该CtClass
		return builder.make().load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER).getLoaded();
	}

	public Object toInstance(final InvocationHandler handler) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		try {
			
			
			builder.method(ElementMatchers.anyOf(WebMethod.class))
					.intercept(MethodDelegation.to(TimingInterceptor.class)).make().load(getClass().getClassLoader())
					.getLoaded();

			/*
			 * builder.transform((builder, type, classLoader, module) -> builder
			 * .method(ElementMatchers.any())
			 * .intercept(MethodDelegation.to(TimingInterceptor.class)));
			 */
			/*
			 * builder.transform(ElementMatchers.annotationType(WebMethod.class)., new
			 * Transformer<Extendable>() {
			 * 
			 * 
			 * 
			 * @Override public Extendable transform(TypeDescription instrumentedType,
			 * Extendable target) { builder .method(ElementMatchers.any())
			 * .intercept(MethodDelegation.to(TimingInterceptor.class)); }
			 * 
			 * });
			 */

			/*
			 * builder.defineConstructor(Arrays.<Class<?>>asList(InvocationHandler.class),
			 * Visibility.PUBLIC);
			 * 
			 * builder.defineConstructor(InvocationHandler.class, Visibility.PUBLIC);
			 */

			// 通过类加载器加载该CtClass，并通过构造器初始化对象
			return builder.make().load(ClassLoader.getSystemClassLoader()).getLoaded()
					.getConstructor(InvocationHandler.class).newInstance(handler);
		} finally {
		}
	}

}