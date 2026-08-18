/*
 * Copyright (c) 2018, hiwepy (https://github.com/easy-4-java).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.cxf.endpoint.utils;


import jakarta.jws.HandlerChain;
import jakarta.jws.WebService;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.ServiceMode;
import jakarta.xml.ws.WebServiceProvider;
import jakarta.xml.ws.soap.Addressing;
import jakarta.xml.ws.soap.AddressingFeature;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.endpoint.annotation.WebBound;
import org.apache.cxf.endpoint.jaxws.definition.SoapBound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.bytebuddy.dynamic.DynamicType.Builder;

public class JaxwsEndpointApiUtils {
	
	protected static final Logger LOG = LoggerFactory.getLogger(JaxwsEndpointApiUtils.class);

	/**
	 * 构造 @WebServiceProvider 注解
	 * @param wsdlLocation		：Location of the WSDL description for the service.
	 * @param serviceName		：Service name.
	 * @param targetNamespace	：Target namespace for the service
	 * @param portName			：Port name.
	 * @return
	 */
	public static Builder<? extends Object> annotWebServiceProvider(final Builder<? extends Object> builder, String wsdlLocation,
			String serviceName, String targetNamespace, String portName) {
		
		return builder.annotateType(new WebServiceProvider() {

			@Override
			public Class<? extends java.lang.annotation.Annotation> annotationType() {
				return WebServiceProvider.class;
			}

			@Override
			public String wsdlLocation() {
				return StringUtils.isNotBlank(wsdlLocation) ? wsdlLocation : "";
			}

			@Override
			public String serviceName() {
				return StringUtils.isNotBlank(serviceName) ? serviceName : "";
			}

			@Override
			public String targetNamespace() {
				return StringUtils.isNotBlank(targetNamespace) ? targetNamespace : "";
			}

			@Override
			public String portName() {
				return StringUtils.isNotBlank(portName) ? portName : "";
			}
			
		});

	}
	
	/**
	 * 构造 @WebService 注解
	 */
	public static Builder<? extends Object> annotWebService(final Builder<? extends Object> builder, final String name, final String targetNamespace, String serviceName,
			String portName, String wsdlLocation, String endpointInterface) {

		return builder.annotateType(new WebService() {

			@Override
			public Class<? extends java.lang.annotation.Annotation> annotationType() {
				return WebService.class;
			}
			
			@Override
			public String name() {
				return StringUtils.isNotBlank(name) ? name : "";
			}

			@Override
			public String targetNamespace() {
				return StringUtils.isNotBlank(targetNamespace) ? targetNamespace : "";
			}

			@Override
			public String serviceName() {
				return StringUtils.isNotBlank(serviceName) ? serviceName : "";
			}

			@Override
			public String portName() {
				return StringUtils.isNotBlank(portName) ? portName : "";
			}

			@Override
			public String wsdlLocation() {
				return StringUtils.isNotBlank(wsdlLocation) ? wsdlLocation : "";
			}
			
			@Override
			public String endpointInterface() {
				return StringUtils.isNotBlank(endpointInterface) ? endpointInterface : "";
			}
			
		});

	}
	
	/**
	 * 构造 @Addressing 注解
	 */
	public static Builder<? extends Object> annotAddressing(final Builder<? extends Object> builder, final boolean enabled, final boolean required,
			final AddressingFeature.Responses responses) {
		
		return builder.annotateType(new Addressing() {

			@Override
			public Class<? extends java.lang.annotation.Annotation> annotationType() {
				return Addressing.class;
			}

			@Override
			public boolean enabled() {
				return enabled;
			}

			@Override
			public boolean required() {
				return required;
			}

			@Override
			public AddressingFeature.Responses responses() {
				return responses;
			}
			
		});

	}

	/**
	 * 构造 @ServiceMode 注解
	 * @return 
	 */
	public static Builder<? extends Object> annotServiceMode(final Builder<? extends Object> builder, final Service.Mode mode) {
		
		return builder.annotateType(new ServiceMode() {

			@Override
			public Service.Mode value() {
				return mode;
			}

			@Override
			public Class<? extends java.lang.annotation.Annotation> annotationType() {
				return ServiceMode.class;
			}
			
		});
	}
	
	/**
	 * 构造 @HandlerChain 注解
	 */
	public static Builder<? extends Object> annotHandlerChain(final Builder<? extends Object> builder, String name, String file) {
		
		return builder.annotateType(new HandlerChain() {

			@Override
			public Class<? extends java.lang.annotation.Annotation> annotationType() {
				return HandlerChain.class;
			}

			@Override
			public String file() {
				return StringUtils.isNotBlank(file) ? file : "" ;
			}

			@Override
			public String name() {
				return StringUtils.isNotBlank(name) ? name : "" ;
			}
			
		});
		
	}
	
	/**
	 * 构造 @WebBound 注解
	 */
	public static Builder<? extends Object> annotWebBound(final Builder<? extends Object> builder, final SoapBound bound) {

		return builder.annotateType(new WebBound() {

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
		
	}
	
	/**
	 * 构造 @WebMethod 注解
	 
	public static Builder<? extends Object> annotWebMethod(final Builder<? extends Object> builder, final SoapMethod method) {
		
		CtAnnotationBuilder builder = CtAnnotationBuilder.create(WebMethod.class, constPool)
				.addStringMember("operationName", method.getOperationName());
		if (StringUtils.isNotBlank(method.getAction())) {
			builder.addStringMember("action", method.getAction());
		}
		builder.addBooleanMember("exclude", method.isExclude());
		return builder.build();
		
	}*/
	
	/**
	 * 构造 @WebParam 参数注解
	
	public static <T> Annotation[][] annotParams(final Builder<? extends Object> builder, SoapParam<?>... params) {

		// 添加 @WebParam 参数注解
		if (params != null && params.length > 0) {

			// 参数模式定义
			// Map<String, EnumMemberValue> modeMap = modeMap(constPool, params);
			
			Annotation[][] paramArrays = new Annotation[params.length][1];
			
			for (int i = 0; i < params.length; i++) {
				
				CtAnnotationBuilder builder = CtAnnotationBuilder.create(WebParam.class, constPool)
						.addStringMember("name", params[i].getName())
						.addStringMember("targetNamespace", params[i].getTargetNamespace())
						.addEnumMember("mode", params[i].getMode())
						.addBooleanMember("header", params[i].isHeader());
				if (StringUtils.isNotBlank(params[i].getPartName())) {
					builder.addStringMember("partName", params[i].getPartName());
				}
				paramArrays[i][0] = builder.build();
				
				 

			}

			return paramArrays;

		}
		return null;
	} */
	
	/**
	 * 构造 @WebResult 注解
	
	public static <T> Annotation annotWebResult(final Builder<? extends Object> builder, final SoapResult<T> result) {
		
		CtAnnotationBuilder builder = CtAnnotationBuilder.create(WebResult.class, constPool)
				.addStringMember("name", result.getName())
				.addBooleanMember("header", result.isHeader());
		if (StringUtils.isNotBlank(result.getPartName())) {
			builder.addStringMember("partName", result.getPartName());
		}
		 if (StringUtils.isNotBlank(result.getTargetNamespace())) {
			 builder.addStringMember("targetNamespace", result.getTargetNamespace());
        }
		return builder.build();
		
	} */

}
