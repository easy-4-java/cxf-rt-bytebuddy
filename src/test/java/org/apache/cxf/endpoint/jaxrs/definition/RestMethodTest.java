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

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * Unit tests for {@link RestMethod}.
 *
 * <p>Exercises both constructors and the getter/setter surface of the
 * mutable fields, while confirming the final fields are stable.</p>
 *
 * @since 1.0.0
 */
public class RestMethodTest {

    /**
     * The three-argument constructor must propagate the method, name,
     * and path; {@code consumes} must remain {@code null} and
     * {@code mediaTypes} must default to the wildcard media type.
     */
    @Test
    public void shouldBuildWithRequiredAttributesOnly() {
        RestMethod method = new RestMethod(HttpMethodEnum.GET, "list", "/items");

        assertSame(HttpMethodEnum.GET, method.getMethod());
        assertEquals("list", method.getName());
        assertEquals("/items", method.getPath());
        assertNull(method.getConsumes());
        assertArrayEquals(new String[]{"*/*"}, method.getMediaTypes());
    }

    /**
     * The four-argument constructor must record the supplied
     * {@code consumes} array verbatim.
     */
    @Test
    public void shouldBuildWithConsumes() {
        String[] consumes = new String[]{"application/json", "text/xml"};
        RestMethod method = new RestMethod(HttpMethodEnum.POST, "create", "/items", consumes);

        assertSame(HttpMethodEnum.POST, method.getMethod());
        assertEquals("create", method.getName());
        assertEquals("/items", method.getPath());
        assertArrayEquals(consumes, method.getConsumes());
    }

    /**
     * The {@code mediaTypes} setter must overwrite the default array.
     */
    @Test
    public void shouldOverrideMediaTypes() {
        RestMethod method = new RestMethod(HttpMethodEnum.GET, "list", "/items");
        String[] mediaTypes = new String[]{"application/json"};
        method.setMediaTypes(mediaTypes);

        assertArrayEquals(mediaTypes, method.getMediaTypes());
    }

    /**
     * The {@code consumes} setter must accept a new value (including
     * {@code null}).
     */
    @Test
    public void shouldOverrideConsumes() {
        RestMethod method = new RestMethod(HttpMethodEnum.GET, "list", "/items");

        method.setConsumes(new String[]{"text/plain"});
        assertArrayEquals(new String[]{"text/plain"}, method.getConsumes());

        method.setConsumes(null);
        assertNull(method.getConsumes());
    }

    /**
     * The final fields must remain identical after constructing a
     * descriptor via the four-argument constructor.
     */
    @Test
    public void shouldExposeFinalFields() {
        RestMethod method = new RestMethod(
                HttpMethodEnum.DELETE, "remove", "/items/{id}",
                "application/json");

        assertEquals("remove", method.getName());
        assertEquals("/items/{id}", method.getPath());
        assertSame(HttpMethodEnum.DELETE, method.getMethod());
    }

    /**
     * A descriptor built with {@code consumes} must still expose the
     * wildcard media type by default.
     */
    @Test
    public void shouldDefaultMediaTypesToWildcard() {
        RestMethod method = new RestMethod(
                HttpMethodEnum.PUT, "update", "/items/{id}", "application/json");

        assertNotNull(method.getMediaTypes());
        assertEquals(1, method.getMediaTypes().length);
        assertEquals("*/*", method.getMediaTypes()[0]);
    }

    /**
     * The descriptor must work for every HTTP verb the enum exposes.
     */
    @Test
    public void shouldSupportAllHttpVerbs() {
        RestMethod get = new RestMethod(HttpMethodEnum.GET, "g", "/g");
        RestMethod post = new RestMethod(HttpMethodEnum.POST, "p", "/p");
        RestMethod put = new RestMethod(HttpMethodEnum.PUT, "u", "/u");
        RestMethod delete = new RestMethod(HttpMethodEnum.DELETE, "d", "/d");
        RestMethod patch = new RestMethod(HttpMethodEnum.PATCH, "pa", "/pa");
        RestMethod head = new RestMethod(HttpMethodEnum.HEAD, "h", "/h");
        RestMethod options = new RestMethod(HttpMethodEnum.OPTIONS, "o", "/o");

        assertSame(HttpMethodEnum.GET, get.getMethod());
        assertSame(HttpMethodEnum.POST, post.getMethod());
        assertSame(HttpMethodEnum.PUT, put.getMethod());
        assertSame(HttpMethodEnum.DELETE, delete.getMethod());
        assertSame(HttpMethodEnum.PATCH, patch.getMethod());
        assertSame(HttpMethodEnum.HEAD, head.getMethod());
        assertSame(HttpMethodEnum.OPTIONS, options.getMethod());
    }
}
