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

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class RestDescriptorBoundaryTest {

    @Test
    public void shouldInitialiseThreeArgCtorToDefaults() {
        RestMethod m = new RestMethod(HttpMethodEnum.GET, "op", "resource");
        assertSame(HttpMethodEnum.GET, m.getMethod());
        assertEquals("op", m.getName());
        assertEquals("resource", m.getPath());
        assertNull("consumes must be null after 3-arg ctor", m.getConsumes());
        assertArrayEquals(new String[]{"*/*"}, m.getMediaTypes());
    }

    @Test
    public void shouldInitialiseConsumesWhenSupplied() {
        RestMethod m = new RestMethod(HttpMethodEnum.POST, "create", "things",
                "application/json", "text/plain");
        assertArrayEquals(
                new String[]{"application/json", "text/plain"},
                m.getConsumes());
    }

    @Test
    public void shouldProduceEmptyArrayWhenNoConsumesAreVarargs() {
        RestMethod m = new RestMethod(HttpMethodEnum.PUT, "update", "x");
        assertNull("3-arg ctor leaves consumes null", m.getConsumes());

        RestMethod m2 = new RestMethod(HttpMethodEnum.PUT, "update", "x", new String[0]);
        assertArrayEquals(new String[0], m2.getConsumes());
    }

    @Test
    public void shouldTolerateEmptyPath() {
        RestMethod rootEmpty = new RestMethod(HttpMethodEnum.GET, "root", "");
        assertEquals("", rootEmpty.getPath());
    }

    @Test
    public void shouldReplaceMediaTypesWithMultiValuedArray() {
        RestMethod m = new RestMethod(HttpMethodEnum.GET, "list", "items");
        m.setMediaTypes(new String[]{"application/json", "application/xml"});
        assertArrayEquals(
                new String[]{"application/json", "application/xml"},
                m.getMediaTypes());
    }

    @Test
    public void shouldAcceptNullAndEmptyMediaTypes() {
        RestMethod m = new RestMethod(HttpMethodEnum.HEAD, "h", "");
        m.setMediaTypes(null);
        assertNull(m.getMediaTypes());
        m.setMediaTypes(new String[0]);
        assertArrayEquals(new String[0], m.getMediaTypes());
    }

    @Test
    public void shouldRoundTripConsumesThroughSetter() {
        RestMethod m = new RestMethod(HttpMethodEnum.POST, "create", "things");
        assertNull("consumes must default to null", m.getConsumes());

        String[] consumes = new String[]{"application/json", "text/plain"};
        m.setConsumes(consumes);
        assertArrayEquals(consumes, m.getConsumes());

        m.setConsumes(null);
        assertNull(m.getConsumes());
    }

    @Test
    public void shouldAcceptAllSevenHttpVerbsViaConstructor() {
        for (HttpMethodEnum verb : HttpMethodEnum.values()) {
            RestMethod m = new RestMethod(verb, verb.name().toLowerCase(), "p/" + verb.name());
            assertSame(verb, m.getMethod());
            assertEquals(verb.name().toLowerCase(), m.getName());
            assertEquals("p/" + verb.name(), m.getPath());
        }
    }

    @Test
    public void shouldExhibitLastWriteWinsSemantics() {
        RestMethod m = new RestMethod(HttpMethodEnum.GET, "a", "p");
        m.setMediaTypes(new String[]{"first"});
        m.setMediaTypes(new String[]{"second"});
        assertTrue(Arrays.equals(new String[]{"second"}, m.getMediaTypes()));

        m.setConsumes(new String[]{"c1"});
        m.setConsumes(new String[]{"c2"});
        assertArrayEquals(new String[]{"c2"}, m.getConsumes());
    }

    @Test
    public void shouldAcceptAnyJavaTypeWithoutRestriction() {
        RestParam<int[]> intArr = new RestParam<int[]>(int[].class, "ids");
        assertSame(int[].class, intArr.getType());

        RestParam<BigDecimal> bd = new RestParam<BigDecimal>(BigDecimal.class, "amount");
        assertSame(BigDecimal.class, bd.getType());

        RestParam<Object> any = new RestParam<Object>(Object.class, "blob");
        assertSame(Object.class, any.getType());
    }

    @Test
    public void shouldRoundTripDefThroughSetter() {
        RestParam<String> p = new RestParam<String>(String.class, "q", "default-q");
        assertEquals("default-q", p.getDef());
        p.setDef(null);
        assertNull(p.getDef());
        p.setDef("new-default");
        assertEquals("new-default", p.getDef());
    }

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void shouldAllowTypeToBeOverwrittenViaSetter() {
        RestParam p = new RestParam(Object.class, "n");
        assertSame(Object.class, p.getType());
        p.setType(String.class);
        assertSame(String.class, p.getType());
        p.setType(Integer.class);
        assertSame(Integer.class, p.getType());
    }

    @Test
    public void shouldRoundTripEveryHttpParamEnumThroughSetter() {
        RestParam<String> p = new RestParam<String>(String.class, "x");
        for (HttpParamEnum bind : HttpParamEnum.values()) {
            p.setFrom(bind);
            assertSame(bind, p.getFrom());
        }
    }

    @Test
    public void shouldPreserveAllFourCtorArguments() {
        RestParam<Long> p = new RestParam<Long>(Long.class, "pk", HttpParamEnum.PATH, "0");
        assertSame(Long.class, p.getType());
        assertEquals("pk", p.getName());
        assertSame(HttpParamEnum.PATH, p.getFrom());
        assertEquals("0", p.getDef());
    }

    @Test
    public void shouldAllowOverwritingNameViaSetter() {
        RestParam<String> p = new RestParam<String>(String.class, "initial");
        assertEquals("initial", p.getName());
        p.setName("renamed");
        assertEquals("renamed", p.getName());
        p.setName(null);
        assertNull(p.getName());
    }

    @Test
    public void shouldDefaultJsonToEmptyStringWhenOnlyUidIsSupplied() {
        RestBound b = new RestBound("u-123");
        assertEquals("u-123", b.getUid());
        assertEquals("", b.getJson());
    }

    @Test
    public void shouldAllowOverwritingUidAndJson() {
        RestBound b = new RestBound("old", "{\"a\":1}");
        b.setUid("new");
        b.setJson("{\"b\":2}");
        assertEquals("new", b.getUid());
        assertEquals("{\"b\":2}", b.getJson());

        b.setUid(null);
        b.setJson(null);
        assertNull(b.getUid());
        assertNull(b.getJson());
    }

    @Test
    public void shouldCaptureBothCtorArguments() {
        RestBound b = new RestBound("id", "payload");
        assertEquals("id", b.getUid());
        assertEquals("payload", b.getJson());
    }

    @Test
    public void shouldSupportEmptyPathForRootResource() {
        RestProduce p = new RestProduce("");
        assertEquals("", p.getPath());
        assertArrayEquals(new String[]{"*/*"}, p.getMediaTypes());
    }

    @Test
    public void shouldReplaceMediaTypesAndNullItOut() {
        RestProduce p = new RestProduce("things");
        p.setMediaTypes(new String[]{"text/html"});
        assertArrayEquals(new String[]{"text/html"}, p.getMediaTypes());
        p.setMediaTypes(null);
        assertNull(p.getMediaTypes());
        p.setMediaTypes(new String[]{"application/json", "application/xml", "text/csv"});
        assertArrayEquals(
                new String[]{"application/json", "application/xml", "text/csv"},
                p.getMediaTypes());
    }
}
