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

import javax.ws.rs.HttpMethod;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

/**
 * Unit tests for {@link HttpMethodEnum}.
 *
 * <p>Exercises the {@link HttpMethodEnum#getKey()} accessor, the
 * case-insensitive lookup, and the negative case where the supplied
 * key does not match any enum value.</p>
 *
 * @since 3.0.0
 */
public class HttpMethodEnumTest {

    /**
     * Each enum value must carry the canonical HTTP verb string defined
     * by the JAX-RS {@link HttpMethod} constants.
     */
    @Test
    public void shouldExposeCanonicalHttpVerbKeys() {
        assertEquals(HttpMethod.GET, HttpMethodEnum.GET.getKey());
        assertEquals(HttpMethod.POST, HttpMethodEnum.POST.getKey());
        assertEquals(HttpMethod.PUT, HttpMethodEnum.PUT.getKey());
        assertEquals(HttpMethod.DELETE, HttpMethodEnum.DELETE.getKey());
        assertEquals(HttpMethod.PATCH, HttpMethodEnum.PATCH.getKey());
        assertEquals(HttpMethod.HEAD, HttpMethodEnum.HEAD.getKey());
        assertEquals(HttpMethod.OPTIONS, HttpMethodEnum.OPTIONS.getKey());
    }

    /**
     * The getter must always return a non-null key for every enum
     * value.
     */
    @Test
    public void shouldReturnNonNullKeys() {
        for (HttpMethodEnum value : HttpMethodEnum.values()) {
            assertNotNull("key for " + value + " must not be null", value.getKey());
        }
    }

    /**
     * The case-insensitive lookup must match the relevant enum value
     * for each canonical HTTP verb.
     */
    @Test
    public void shouldResolveKeysCaseInsensitively() {
        assertSame(HttpMethodEnum.GET, HttpMethodEnum.valueOfIgnoreCase("GET"));
        assertSame(HttpMethodEnum.GET, HttpMethodEnum.valueOfIgnoreCase("get"));
        assertSame(HttpMethodEnum.GET, HttpMethodEnum.valueOfIgnoreCase("Get"));
        assertSame(HttpMethodEnum.POST, HttpMethodEnum.valueOfIgnoreCase("POST"));
        assertSame(HttpMethodEnum.POST, HttpMethodEnum.valueOfIgnoreCase("post"));
        assertSame(HttpMethodEnum.PUT, HttpMethodEnum.valueOfIgnoreCase("PUT"));
        assertSame(HttpMethodEnum.DELETE, HttpMethodEnum.valueOfIgnoreCase("DELETE"));
        assertSame(HttpMethodEnum.PATCH, HttpMethodEnum.valueOfIgnoreCase("PATCH"));
        assertSame(HttpMethodEnum.HEAD, HttpMethodEnum.valueOfIgnoreCase("HEAD"));
        assertSame(HttpMethodEnum.OPTIONS, HttpMethodEnum.valueOfIgnoreCase("OPTIONS"));
    }

    /**
     * When the supplied key is unknown, the lookup must throw a
     * {@link NoSuchElementException} carrying the offending key in the
     * message.
     */
    @Test
    public void shouldFailForUnknownKey() {
        try {
            HttpMethodEnum.valueOfIgnoreCase("TRACE");
            fail("expected NoSuchElementException");
        } catch (NoSuchElementException expected) {
            assertNotNull(expected.getMessage());
            assertEquals("Cannot found ApiType with key 'TRACE'.", expected.getMessage());
        }
    }

    /**
     * The same failure path must apply for an empty key, which is
     * clearly not a valid HTTP verb.
     */
    @Test
    public void shouldFailForEmptyKey() {
        try {
            HttpMethodEnum.valueOfIgnoreCase("");
            fail("expected NoSuchElementException");
        } catch (NoSuchElementException expected) {
            assertNotNull(expected.getMessage());
        }
    }

    /**
     * The standard {@link Enum#valueOf(Class, String)} lookup must
     * succeed for every constant.
     */
    @Test
    public void shouldSupportValueOfByName() {
        assertSame(HttpMethodEnum.GET, HttpMethodEnum.valueOf("GET"));
        assertSame(HttpMethodEnum.POST, HttpMethodEnum.valueOf("POST"));
        assertSame(HttpMethodEnum.PUT, HttpMethodEnum.valueOf("PUT"));
        assertSame(HttpMethodEnum.DELETE, HttpMethodEnum.valueOf("DELETE"));
        assertSame(HttpMethodEnum.PATCH, HttpMethodEnum.valueOf("PATCH"));
        assertSame(HttpMethodEnum.HEAD, HttpMethodEnum.valueOf("HEAD"));
        assertSame(HttpMethodEnum.OPTIONS, HttpMethodEnum.valueOf("OPTIONS"));
    }

    /**
     * The enum must expose exactly seven values, matching the JAX-RS
     * {@link HttpMethod} constants.
     */
    @Test
    public void shouldExposeSevenValues() {
        assertEquals(7, HttpMethodEnum.values().length);
    }
}
