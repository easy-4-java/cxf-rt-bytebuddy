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

/**
 * Immutable descriptor of a JAX-RS resource (or sub-resource) that exposes
 * a fixed URI path and one or more produced media types.
 *
 * <p>The descriptor is the runtime analogue of a {@link javax.ws.rs.Path}
 * annotation combined with a {@link javax.ws.rs.Produces} annotation. It
 * is used by the byte-buddy generation pipeline to carry the route
 * information from configuration to the synthesised endpoint subclass.</p>
 *
 * <p>Both fields are surfaced for inspection; only the produced media
 * types are mutable so that callers can refine the descriptor after
 * construction (for example to swap the default wildcard media type
 * for a narrower set of media types).</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 * @see RestMethod
 * @see javax.ws.rs.Path
 * @see javax.ws.rs.Produces
 */
public class RestProduce {

    /**
     * Defines a URI template for the resource class or method, must not
     * include matrix parameters.
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
     * wildcard media type (any subtype) so the descriptor is valid
     * even when no media type constraint is supplied.
     */
    private String[] mediaTypes = new String[] { "*/*" };

    /**
     * Creates a new descriptor with the supplied URI path and produced
     * media types.
     *
     * @param path       the URI template; must not be {@code null}.
     * @param mediaTypes the produced media types; when empty the descriptor
     *                   defaults to the wildcard media type (any subtype).
     */
    public RestProduce(String path, String... mediaTypes) {
        this.path = path;
        if (mediaTypes != null && mediaTypes.length > 0) {
            this.mediaTypes = mediaTypes;
        }
    }

    /**
     * Returns the produced media types.
     *
     * @return the produced media types, never {@code null} (defaults to
     *         the wildcard media type when the constructor received no
     *         media types).
     */
    public String[] getMediaTypes() {
        return mediaTypes;
    }

    /**
     * Stores the produced media types.
     *
     * @param mediaTypes the new produced media types; must not be
     *                   {@code null}.
     */
    public void setMediaTypes(String[] mediaTypes) {
        this.mediaTypes = mediaTypes;
    }

    /**
     * Returns the URI template exposed by the descriptor.
     *
     * @return the URI template, never {@code null}.
     */
    public String getPath() {
        return path;
    }

}
