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

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class HttpEnumExhaustiveTest {

    @Test
    public void shouldEnumerateAllSevenHttpParamValues() {
        HttpParamEnum[] values = HttpParamEnum.values();
        assertEquals(7, values.length);
        assertArrayEquals(
                new HttpParamEnum[]{
                        HttpParamEnum.BEAN,
                        HttpParamEnum.COOKIE,
                        HttpParamEnum.HEADER,
                        HttpParamEnum.MATRIX,
                        HttpParamEnum.FORM,
                        HttpParamEnum.PATH,
                        HttpParamEnum.QUERY},
                values);
    }

    @Test
    public void shouldPreserveDeclaredOrdinalForParamBindings() {
        assertEquals(0, HttpParamEnum.BEAN.ordinal());
        assertEquals(1, HttpParamEnum.COOKIE.ordinal());
        assertEquals(2, HttpParamEnum.HEADER.ordinal());
        assertEquals(3, HttpParamEnum.MATRIX.ordinal());
        assertEquals(4, HttpParamEnum.FORM.ordinal());
        assertEquals(5, HttpParamEnum.PATH.ordinal());
        assertEquals(6, HttpParamEnum.QUERY.ordinal());
    }

    @Test
    public void shouldValueOfByNameForAllParamBindings() {
        for (HttpParamEnum v : HttpParamEnum.values()) {
            assertSame(v, HttpParamEnum.valueOf(HttpParamEnum.class, v.name()));
        }
    }

    @Test
    public void shouldHaveDistinctParamBindingNames() {
        Set<String> seen = new HashSet<String>();
        for (HttpParamEnum v : HttpParamEnum.values()) {
            assertTrue("Duplicate param name: " + v.name(), seen.add(v.name()));
        }
    }

    @Test
    public void shouldMatchJaxRsAnnotationSimpleName() {
        assertEquals("BEAN", HttpParamEnum.BEAN.name());
        assertEquals("COOKIE", HttpParamEnum.COOKIE.name());
        assertEquals("HEADER", HttpParamEnum.HEADER.name());
        assertEquals("MATRIX", HttpParamEnum.MATRIX.name());
        assertEquals("FORM", HttpParamEnum.FORM.name());
        assertEquals("PATH", HttpParamEnum.PATH.name());
        assertEquals("QUERY", HttpParamEnum.QUERY.name());
    }

    @Test
    public void shouldEnumerateAllSevenHttpMethodVerbs() {
        HttpMethodEnum[] values = HttpMethodEnum.values();
        assertEquals(7, values.length);
        assertArrayEquals(
                new HttpMethodEnum[]{
                        HttpMethodEnum.GET,
                        HttpMethodEnum.POST,
                        HttpMethodEnum.PUT,
                        HttpMethodEnum.DELETE,
                        HttpMethodEnum.PATCH,
                        HttpMethodEnum.HEAD,
                        HttpMethodEnum.OPTIONS},
                values);
    }

    @Test
    public void shouldExposeJaxRsHttpMethodKeyForEachVerb() {
        assertEquals("GET", HttpMethodEnum.GET.getKey());
        assertEquals("POST", HttpMethodEnum.POST.getKey());
        assertEquals("PUT", HttpMethodEnum.PUT.getKey());
        assertEquals("DELETE", HttpMethodEnum.DELETE.getKey());
        assertEquals("PATCH", HttpMethodEnum.PATCH.getKey());
        assertEquals("HEAD", HttpMethodEnum.HEAD.getKey());
        assertEquals("OPTIONS", HttpMethodEnum.OPTIONS.getKey());
    }

    @Test
    public void shouldHaveDistinctHttpMethodKeyStrings() {
        Set<String> seen = new HashSet<String>();
        for (HttpMethodEnum v : HttpMethodEnum.values()) {
            assertTrue("Duplicate HTTP verb key: " + v.getKey(),
                    seen.add(v.getKey()));
        }
    }

    @Test
    public void shouldValueOfByNameForAllHttpMethods() {
        for (HttpMethodEnum v : HttpMethodEnum.values()) {
            assertSame(v, HttpMethodEnum.valueOf(HttpMethodEnum.class, v.name()));
        }
    }

    @Test
    public void shouldPreserveOrdinalForMethodVerbs() {
        assertEquals(0, HttpMethodEnum.GET.ordinal());
        assertEquals(1, HttpMethodEnum.POST.ordinal());
        assertEquals(2, HttpMethodEnum.PUT.ordinal());
        assertEquals(3, HttpMethodEnum.DELETE.ordinal());
        assertEquals(4, HttpMethodEnum.PATCH.ordinal());
        assertEquals(5, HttpMethodEnum.HEAD.ordinal());
        assertEquals(6, HttpMethodEnum.OPTIONS.ordinal());
    }

    @Test
    public void shouldFindAllVerbsThroughUpperCaseIgnoreCaseLookup() {
        for (HttpMethodEnum v : HttpMethodEnum.values()) {
            HttpMethodEnum found = HttpMethodEnum.valueOfIgnoreCase(v.getKey());
            assertSame(v, found);
        }
    }

    @Test
    public void shouldFindAllVerbsThroughLowerCaseIgnoreCaseLookup() {
        assertSame(HttpMethodEnum.GET, HttpMethodEnum.valueOfIgnoreCase("get"));
        assertSame(HttpMethodEnum.POST, HttpMethodEnum.valueOfIgnoreCase("post"));
        assertSame(HttpMethodEnum.PUT, HttpMethodEnum.valueOfIgnoreCase("put"));
        assertSame(HttpMethodEnum.DELETE, HttpMethodEnum.valueOfIgnoreCase("delete"));
        assertSame(HttpMethodEnum.PATCH, HttpMethodEnum.valueOfIgnoreCase("patch"));
        assertSame(HttpMethodEnum.HEAD, HttpMethodEnum.valueOfIgnoreCase("head"));
        assertSame(HttpMethodEnum.OPTIONS, HttpMethodEnum.valueOfIgnoreCase("options"));
    }

    @Test
    public void shouldFindVerbsWithMixedCase() {
        assertSame(HttpMethodEnum.GET, HttpMethodEnum.valueOfIgnoreCase("Get"));
        assertSame(HttpMethodEnum.POST, HttpMethodEnum.valueOfIgnoreCase("PoSt"));
        assertSame(HttpMethodEnum.OPTIONS, HttpMethodEnum.valueOfIgnoreCase("OpTiOnS"));
    }

    @Test
    public void shouldThrowForUnknownVerbWithDescriptiveMessage() {
        try {
            HttpMethodEnum.valueOfIgnoreCase("trace");
            fail("Expected NoSuchElementException");
        } catch (NoSuchElementException expected) {
            assertNotNull(expected.getMessage());
            assertTrue("expected enum name in message",
                    expected.getMessage().contains("HttpMethodEnum"));
            assertTrue("expected key in message",
                    expected.getMessage().contains("trace"));
        }
    }

    @Test
    public void shouldThrowForNullOrEmptyKeysGracefully() {
        try {
            HttpMethodEnum.valueOfIgnoreCase(null);
            fail("Expected NoSuchElementException for null");
        } catch (NoSuchElementException expected) {
        }
        try {
            HttpMethodEnum.valueOfIgnoreCase("");
            fail("Expected NoSuchElementException for empty");
        } catch (NoSuchElementException expected) {
        }
    }

    @Test
    public void shouldThrowForKeysWithNonLetters() {
        try {
            HttpMethodEnum.valueOfIgnoreCase("GET /path");
            fail("Expected NoSuchElementException");
        } catch (NoSuchElementException expected) {
            assertTrue(expected.getMessage().contains("GET /path"));
        }
    }
}
