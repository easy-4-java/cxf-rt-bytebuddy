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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Unit tests for {@link HttpParamEnum}.
 *
 * <p>Exercises the canonical JAX-RS binding locations, value lookup by
 * name, and the iteration order of {@link HttpParamEnum#values()}.</p>
 *
 * @since 2.0.0
 */
public class HttpParamEnumTest {

    /**
     * The enum must expose exactly the seven canonical JAX-RS binding
     * locations.
     */
    @Test
    public void shouldExposeAllCanonicalValues() {
        Set<HttpParamEnum> values = new HashSet<>(Arrays.asList(HttpParamEnum.values()));
        assertEquals(7, values.size());
        assertTrue(values.contains(HttpParamEnum.BEAN));
        assertTrue(values.contains(HttpParamEnum.COOKIE));
        assertTrue(values.contains(HttpParamEnum.HEADER));
        assertTrue(values.contains(HttpParamEnum.MATRIX));
        assertTrue(values.contains(HttpParamEnum.FORM));
        assertTrue(values.contains(HttpParamEnum.PATH));
        assertTrue(values.contains(HttpParamEnum.QUERY));
    }

    /**
     * Each enum value must be reference-distinct because the runtime
     * relies on identity (e.g. as map keys) when grouping parameters.
     */
    @Test
    public void shouldExposeDistinctReferences() {
        HttpParamEnum a = HttpParamEnum.QUERY;
        HttpParamEnum b = HttpParamEnum.QUERY;
        assertSame("valueOf must return the cached enum constant", a, b);

        HttpParamEnum c = HttpParamEnum.PATH;
        assertNotEquals(a, c);
    }

    /**
     * The standard {@link Enum#valueOf(Class, String)} lookup must
     * succeed for every constant.
     */
    @Test
    public void shouldSupportValueOfByName() {
        assertSame(HttpParamEnum.BEAN, HttpParamEnum.valueOf("BEAN"));
        assertSame(HttpParamEnum.COOKIE, HttpParamEnum.valueOf("COOKIE"));
        assertSame(HttpParamEnum.HEADER, HttpParamEnum.valueOf("HEADER"));
        assertSame(HttpParamEnum.MATRIX, HttpParamEnum.valueOf("MATRIX"));
        assertSame(HttpParamEnum.FORM, HttpParamEnum.valueOf("FORM"));
        assertSame(HttpParamEnum.PATH, HttpParamEnum.valueOf("PATH"));
        assertSame(HttpParamEnum.QUERY, HttpParamEnum.valueOf("QUERY"));
    }

    /**
     * Asking for an unknown name through {@link Enum#valueOf(Class, String)}
     * must surface an {@link IllegalArgumentException} (Java contract).
     */
    @Test
    public void shouldFailForUnknownValueOf() {
        try {
            HttpParamEnum.valueOf("UNKNOWN");
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertNotNull(expected.getMessage());
        }
    }

    /**
     * Confirm the ordinals are stable: the enum is small enough that the
     * positions are not load-bearing, but a regression here would
     * indicate accidental reordering.
     */
    @Test
    public void shouldPreserveOrdinals() {
        assertEquals(0, HttpParamEnum.BEAN.ordinal());
        assertEquals(1, HttpParamEnum.COOKIE.ordinal());
        assertEquals(2, HttpParamEnum.HEADER.ordinal());
        assertEquals(3, HttpParamEnum.MATRIX.ordinal());
        assertEquals(4, HttpParamEnum.FORM.ordinal());
        assertEquals(5, HttpParamEnum.PATH.ordinal());
        assertEquals(6, HttpParamEnum.QUERY.ordinal());
    }

    /**
     * The {@code name()} contract must return the original declaration.
     */
    @Test
    public void shouldExposeDeclarationNames() {
        assertEquals("BEAN", HttpParamEnum.BEAN.name());
        assertEquals("COOKIE", HttpParamEnum.COOKIE.name());
        assertEquals("HEADER", HttpParamEnum.HEADER.name());
        assertEquals("MATRIX", HttpParamEnum.MATRIX.name());
        assertEquals("FORM", HttpParamEnum.FORM.name());
        assertEquals("PATH", HttpParamEnum.PATH.name());
        assertEquals("QUERY", HttpParamEnum.QUERY.name());
    }
}
