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

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;

/**
 * Immutable-by-default descriptor of a JAX-RS resource method that the
 * byte-buddy runtime will eventually publish.
 *
 * <p>The descriptor captures the four pieces of information that the
 * runtime needs to synthesise a resource method on the generated
 * endpoint: the Java method name, the HTTP verb exposed via
 * {@link HttpMethodEnum}, the {@linkplain javax.ws.rs.Path URI path}
 * served by the method, and the optional media-type constraints used
 * for content negotiation.</p>
 *
 * <p>The class is intentionally minimal: only the
 * {@linkplain #mediaTypes media type produced} and
 * {@linkplain #consumes request media types accepted} are mutable after
 * construction, all other fields are {@code final}.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see HttpMethodEnum
 * @see javax.ws.rs.Path
 * @see javax.ws.rs.Consumes
 */
public class RestMethod {

    /**
     * The name of the Java method that the runtime will generate.
     *
     * @see #getName()
     */
    private final String name;

    /**
     * Associates the HTTP method exposed by the descriptor with an
     * annotation that the runtime can synthesise.
     *
     * @see GET
     * @see POST
     * @see PUT
     * @see DELETE
     * @see PATCH
     * @see HEAD
     * @see OPTIONS
     */
    private final HttpMethodEnum method;

    /**
     * Identifies the URI path that a resource class or class method will
     * serve requests for.
     *
     * @see javax.ws.rs.Path
     */
    private final String path;

    /**
     * A list of media types. Each entry may specify a single type or
     * consist of a comma separated list of types, with any leading or
     * trailing white-spaces in a single type entry being ignored. For
     * example:
     *
     * <pre>
     * { "image/jpeg, image/gif ", " image/png" }
     * </pre>
     *
     * Use of the comma-separated form allows definition of a common
     * string constant for use on multiple targets. Defaults to the
     * wildcard media type (any subtype) so that the descriptor is
     * valid out of the box.
     */
    private String[] mediaTypes = new String[] { "*/*" };

    /**
     * Defines the media types that the methods of a resource class or
     * {@link javax.ws.rs.ext.MessageBodyReader} can accept.
     *
     * @see javax.ws.rs.Consumes
     */
    private String[] consumes;

    /**
     * Creates a new descriptor with the supplied HTTP method, Java
     * method name, and URI path.
     *
     * @param method the HTTP verb exposed by the method; must not be
     *               {@code null}.
     * @param name   the Java method name; must not be {@code null}.
     * @param path   the URI path served by the method; must not be
     *               {@code null}.
     */
    public RestMethod(HttpMethodEnum method, String name, String path) {
        this.method = method;
        this.name = name;
        this.path = path;
    }

    /**
     * Creates a new descriptor with the supplied HTTP method, Java
     * method name, URI path, and an explicit list of accepted media
     * types.
     *
     * @param method   the HTTP verb exposed by the method; must not be
     *                 {@code null}.
     * @param name     the Java method name; must not be {@code null}.
     * @param path     the URI path served by the method; must not be
     *                 {@code null}.
     * @param consumes the list of media types accepted by the method;
     *                 may be {@code null} or empty.
     */
    public RestMethod(HttpMethodEnum method, String name, String path, String... consumes) {
        this.method = method;
        this.name = name;
        this.path = path;
        this.consumes = consumes;
    }

    /**
     * Returns the list of media types accepted by the method.
     *
     * @return the consumed media types declared via
     *         {@link javax.ws.rs.Consumes}, may be {@code null} when no
     *         constraint was supplied.
     */
    public String[] getConsumes() {
        return consumes;
    }

    /**
     * Returns the list of media types produced by the method.
     *
     * @return the produced media types, never {@code null} (defaults to
     *         the wildcard media type any subtype).
     */
    public String[] getMediaTypes() {
        return mediaTypes;
    }

    /**
     * Stores the list of media types produced by the method.
     *
     * @param mediaTypes the new produced media types; must not be
     *                   {@code null}.
     */
    public void setMediaTypes(String[] mediaTypes) {
        this.mediaTypes = mediaTypes;
    }

    /**
     * Stores the list of media types accepted by the method.
     *
     * @param consumes the new accepted media types; may be {@code null}.
     */
    public void setConsumes(String[] consumes) {
        this.consumes = consumes;
    }

    /**
     * Returns the Java method name.
     *
     * @return the Java method name, never {@code null}.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the HTTP verb exposed by the method.
     *
     * @return the {@link HttpMethodEnum}, never {@code null}.
     */
    public HttpMethodEnum getMethod() {
        return method;
    }

    /**
     * Returns the URI path served by the method.
     *
     * @return the URI path, never {@code null}.
     */
    public String getPath() {
        return path;
    }

}
