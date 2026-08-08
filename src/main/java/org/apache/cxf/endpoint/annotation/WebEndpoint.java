/*
 * Copyright (c) 2018, Loong Wan (https://github.com/loong10k).
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
package org.apache.cxf.endpoint.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares a type as a managed web endpoint that the byte-buddy runtime
 * turns into a CXF JAX-WS or JAX-RS publishable service.
 *
 * <p>Annotated types are scanned by the runtime, which then synthesises
 * the necessary CXF server artefacts (publish address, interceptors,
 * fault handlers, features, and generic JAX-WS handlers) so that the
 * application can be exposed without writing boilerplate XML or
 * programmatic server bootstrap code.</p>
 *
 * <p>The annotation is type-level, {@linkplain Inherited inherited}, and
 * retained at runtime so that the byte-buddy agent can discover it via
 * reflection. All attributes default to an empty array so that an
 * empty annotation is legal and produces a minimally configured endpoint.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see WebBound
 * @see org.apache.cxf.endpoint.EndpointApi
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface WebEndpoint {

    /**
     * The publish address at which the endpoint will be exposed.
     *
     * @return the JAX-WS / JAX-RS publish address string; never
     *         {@code null}.
     */
    String addr();

    /**
     * Names of the inbound interceptor beans to be applied to incoming
     * requests.
     *
     * @return an array of bean names (Spring / lookup based) or a single
     *         empty string when no inbound interceptors are required.
     */
    String[] inInterceptors() default {""};

    /**
     * Names of the outbound interceptor beans to be applied to outgoing
     * responses.
     *
     * @return an array of bean names or a single empty string when no
     *         outbound interceptors are required.
     */
    String[] outInterceptors() default {""};

    /**
     * Names of inbound fault interceptor beans to be applied to faults
     * generated on the server side.
     *
     * @return an array of bean names or a single empty string when no
     *         inbound fault interceptors are required.
     */
    String[] inFaults() default {""};

    /**
     * Names of outbound fault interceptor beans to be applied to faults
     * propagated back to the client.
     *
     * @return an array of bean names or a single empty string when no
     *         outbound fault interceptors are required.
     */
    String[] outFaults() default {""};

    /**
     * Names of CXF {@link org.apache.cxf.feature.Feature} beans to be
     * installed on the endpoint.
     *
     * @return an array of bean names or a single empty string when no
     *         features are required.
     */
    String[] features() default {""};

    /**
     * Names of generic JAX-WS handler beans to be installed on the
     * endpoint.
     *
     * @return an array of bean names or a single empty string when no
     *         handlers are required.
     */
    String[] handlers() default {""};

}
