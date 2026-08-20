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
package org.apache.cxf.endpoint.jaxrs.definition;

import java.util.NoSuchElementException;

import javax.ws.rs.HttpMethod;

/**
 * Enumeration of the HTTP verbs recognised by the byte-buddy runtime, each
 * bound to the corresponding standard {@link javax.ws.rs.HttpMethod} string.
 *
 * <p>The enum lets the runtime reason about HTTP verbs without falling back
 * to string comparisons, and the {@link #valueOfIgnoreCase(String)} helper
 * offers a defensive lookup that throws {@link NoSuchElementException} when
 * the supplied key is not one of the seven supported verbs.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 * @see javax.ws.rs.HttpMethod
 */
public enum HttpMethodEnum {

    /**
     * HTTP GET method.
     */
    GET(HttpMethod.GET),
    /**
     * HTTP POST method.
     */
    POST(HttpMethod.POST),
    /**
     * HTTP PUT method.
     */
    PUT(HttpMethod.PUT),
    /**
     * HTTP DELETE method.
     */
    DELETE(HttpMethod.DELETE),
    /**
     * HTTP PATCH method.
     */
    PATCH(HttpMethod.PATCH),
    /**
     * HTTP HEAD method.
     */
    HEAD(HttpMethod.HEAD),
    /**
     * HTTP OPTIONS method.
     */
    OPTIONS(HttpMethod.OPTIONS);

    /**
     * The canonical HTTP verb string associated with the enum value.
     * The string is supplied by the JAX-RS {@link javax.ws.rs.HttpMethod}
     * constants and is never {@code null}.
     */
    private String key;

    /**
     * Creates a new enum value bound to the supplied JAX-RS HTTP verb.
     *
     * @param key the canonical HTTP verb string; must not be
     *            {@code null}.
     */
    private HttpMethodEnum(String key) {
        this.key = key;
    }

    /**
     * Returns the canonical HTTP verb string.
     *
     * @return the verb string such as {@code "GET"} or {@code "POST"};
     *         never {@code null}.
     */
    public String getKey() {
        return key;
    }

    /**
     * Resolves an enum value from its canonical HTTP verb string in a
     * case-insensitive way.
     *
     * @param key the HTTP verb string to look up, such as {@code "get"}
     *            or {@code "POST"}; must not be {@code null}.
     * @return the matching enum value when {@code key} corresponds to a
     *         known verb (case-insensitive).
     * @throws NoSuchElementException if no enum value matches the supplied
     *                                {@code key}.
     */
    public static HttpMethodEnum valueOfIgnoreCase(String key) {
        for (HttpMethodEnum apiType : HttpMethodEnum.values()) {
            if (apiType.getKey().equalsIgnoreCase(key)) {
                return apiType;
            }
        }
        throw new NoSuchElementException("Cannot found HttpMethodEnum with key '" + key + "'.");
    }

}
