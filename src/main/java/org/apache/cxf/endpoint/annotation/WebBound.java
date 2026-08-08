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
 * Binds a request-scoped payload (or a single correlated key) to the
 * annotated type or method so that the byte-buddy runtime can hydrate a
 * corresponding {@link org.apache.cxf.endpoint.jaxrs.definition.RestBound}
 * helper on the dispatch path.
 *
 * <p>Two pieces of information are carried:</p>
 * <ul>
 *   <li>{@link #uid()} &mdash; a logical identifier, typically used to
 *       propagate a primary key that the implementation can use to look up
 *       associated data via an injected data source.</li>
 *   <li>{@link #json()} &mdash; a JSON-encoded payload that the runtime
 *       will marshal into the bound object. The default of {@code "{}"}
 *       keeps the annotation safe to apply without a body.</li>
 * </ul>
 *
 * <p>The annotation is valid on both type and method elements, is
 * {@linkplain Inherited inherited}, and is retained at runtime so the
 * byte-buddy agent can locate it via reflection.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 2.0.0
 * @see WebEndpoint
 * @see org.apache.cxf.endpoint.jaxrs.definition.RestBound
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface WebBound {

    /**
     * Logical identifier propagated with the binding.
     *
     * <p>Typically populated with the primary key of an entity that the
     * callable resource expects to operate on. Defaults to an empty
     * string, which is treated by the runtime as "no key supplied".</p>
     *
     * @return the binding identifier, never {@code null}.
     */
    String uid() default "";

    /**
     * JSON-encoded payload that the runtime will forward to the bound
     * object.
     *
     * <p>Defaults to an empty JSON object so the annotation can be used
     * without specifying a body. The payload is opaque to the runtime
     * beyond being a valid JSON document.</p>
     *
     * @return the JSON-encoded payload, never {@code null}.
     */
    String json() default "{}";

}
