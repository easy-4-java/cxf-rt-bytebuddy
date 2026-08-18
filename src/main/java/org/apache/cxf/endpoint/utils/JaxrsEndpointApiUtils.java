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

import jakarta.ws.rs.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.endpoint.annotation.WebBound;
import org.apache.cxf.endpoint.jaxrs.definition.HttpParamEnum;
import org.apache.cxf.endpoint.jaxrs.definition.RestBound;
import org.apache.cxf.endpoint.jaxrs.definition.RestMethod;
import org.apache.cxf.endpoint.jaxrs.definition.RestParam;

import java.lang.annotation.Annotation;

public class JaxrsEndpointApiUtils {
 
	/**
	 * 构造  @Path 注解
	 */
	public static Annotation annotPath(final String path) {
		return new Path() {
			
			@Override
			public Class<? extends Annotation> annotationType() {
				return Path.class;
			}
			
			@Override
			public String value() {
				return path;
			}
		};
	}
	
	/**
	 * 构造  @Produces 注解
	 */
	public static Annotation annotProduces( String... mediaTypes) {
		// 参数预处理
		final String[] mediaTypesCopy = ArrayUtils.isEmpty(mediaTypes) ? new String[] {} : mediaTypes;
		return new Produces() {
			
			@Override
			public Class<? extends Annotation> annotationType() {
				return Produces.class;
			}
			
			@Override
			public String[] value() {
				return mediaTypesCopy;
			}
		};
	}
	
	/**
	 * 构造 @WebBound 注解
	 */
	public static Annotation annotWebBound(final ConstPool constPool, final RestBound bound) {
		
		CtAnnotationBuilder builder = CtAnnotationBuilder.create(WebBound.class, constPool).
			addStringMember("uid", bound.getUid());
		if (StringUtils.isNotBlank(bound.getJson())) {
			builder.addStringMember("json", bound.getJson());
        }
		return builder.build();
		
	}
	
	/**
	 * 根据参数 构造   @GET、 @POST、 @PUT、 @DELETE、 @PATCH、 @HEAD、 @OPTIONS 注解
	 */
	public static Annotation annotHttpMethod(final ConstPool constPool, final RestMethod method) {
		
		Annotation annot = null;
		switch (method.getMethod()) {
			case GET:{
				annot = new Annotation(GET.class.getName(), constPool);
			};break;
			case POST:{
				annot = new Annotation(POST.class.getName(), constPool);
			};break;
			case PUT:{
				annot = new Annotation(PUT.class.getName(), constPool);
			};break;
			case DELETE:{
				annot = new Annotation(DELETE.class.getName(), constPool);
			};break;
			case PATCH:{
				annot = new Annotation(PATCH.class.getName(), constPool);
			};break;
			case HEAD:{
				annot = new Annotation(HEAD.class.getName(), constPool);
			};break;
			case OPTIONS:{
				annot = new Annotation(OPTIONS.class.getName(), constPool);
			};break;
			default:{
				annot = new Annotation(GET.class.getName(), constPool);
			};break;
		}
		
		return annot;
	}
	
	/**
	 * 构造 @Consumes 注解
	 */
	public static <T> Annotation annotConsumes(final ConstPool constPool, String... consumes) {
		// 参数预处理
		consumes = ArrayUtils.isEmpty(consumes) ? new String[] {"*/*"} : consumes;
		CtAnnotationBuilder builder = CtAnnotationBuilder.create(Consumes.class, constPool).
				addStringMember("value", consumes);
		return builder.build();
	}
	
	/**
	 * 构造 @BeanParam 、@CookieParam、@FormParam、@HeaderParam、@MatrixParam、@PathParam、@QueryParam 参数注解
	 */
	public static <T> Annotation[][] annotParams(final ConstPool constPool, RestParam<?>... params) {

		// 添加 @WebParam 参数注解
		if (params != null && params.length > 0) {

			Annotation[][] paramArrays = new Annotation[params.length][1];
			
			Annotation paramAnnot = null;
			for (int i = 0; i < params.length; i++) {
				
				switch (params[i].getFrom()) {
					case BEAN:{
						paramAnnot = new Annotation(BeanParam.class.getName(), constPool);
					};break;
					case COOKIE:{
						paramAnnot = new Annotation(CookieParam.class.getName(), constPool);
					};break;
					case FORM:{
						paramAnnot = new Annotation(FormParam.class.getName(), constPool);
					};break;
					case HEADER:{
						paramAnnot = new Annotation(HeaderParam.class.getName(), constPool);
					};break;
					case MATRIX:{
						paramAnnot = new Annotation(MatrixParam.class.getName(), constPool);
					};break;
					case PATH:{
						paramAnnot = new Annotation(PathParam.class.getName(), constPool);
					};break;
					case QUERY:{
						paramAnnot = new Annotation(QueryParam.class.getName(), constPool);
					};break;
					default:{
						paramAnnot = new Annotation(QueryParam.class.getName(), constPool);
					};break;
				}
				if(HttpParamEnum.BEAN.compareTo(params[i].getFrom()) != 0){
					paramAnnot.addMemberValue("value", new StringMemberValue(params[i].getName(), constPool));
				}
				
				// 有默认值
				if(StringUtils.isNotBlank(params[i].getDef())) {
					
					paramArrays[i] = new Annotation[2];
					paramArrays[i][0] = paramAnnot;
					
					Annotation defAnnot = new Annotation(DefaultValue.class.getName(), constPool);
					defAnnot.addMemberValue("value", new StringMemberValue(params[i].getDef(), constPool));
					paramArrays[i][1] = paramAnnot;
					
				} else {
					paramArrays[i][0] = paramAnnot;
				}
				
			}
			
			return paramArrays;

		}
		return null;
	}
	
}
