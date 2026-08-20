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

import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class EndpointAnnotationReflectionTest {

    @WebEndpoint(
            addr = "http://example.com/svc",
            inInterceptors = {"in1", "in2"},
            outInterceptors = {"outA"},
            inFaults = {"faultIn"},
            outFaults = {"faultOut"},
            features = {"featX", "featY", "featZ"},
            handlers = {"h1", "h2"})
    private static final class FullyPopulatedSample {
    }

    @WebEndpoint(addr = "http://example.com/defaults")
    private static final class DefaultsSample {
    }

    @WebBound(uid = "type-bound", json = "{\"scope\":\"class\"}")
    private static final class MethodLevelBoundSample {
        @WebBound(uid = "method-bound", json = "{\"scope\":\"method\"}")
        public void sampleMethod() {
        }

        @WebBound(uid = "only-uid-supplied")
        public void defaultsMethod() {
        }
    }

    @Test
    public void shouldExposeAllDefaultAttributeArrays() {
        WebEndpoint ann = DefaultsSample.class.getAnnotation(WebEndpoint.class);
        assertNotNull(ann);
        assertEquals("http://example.com/defaults", ann.addr());
        assertArrayEquals(new String[]{""}, ann.inInterceptors());
        assertArrayEquals(new String[]{""}, ann.outInterceptors());
        assertArrayEquals(new String[]{""}, ann.inFaults());
        assertArrayEquals(new String[]{""}, ann.outFaults());
        assertArrayEquals(new String[]{""}, ann.features());
        assertArrayEquals(new String[]{""}, ann.handlers());
    }

    @Test
    public void shouldPreserveAllExplicitAttributeValues() {
        WebEndpoint ann = FullyPopulatedSample.class.getAnnotation(WebEndpoint.class);
        assertNotNull(ann);
        assertEquals("http://example.com/svc", ann.addr());
        assertArrayEquals(new String[]{"in1", "in2"}, ann.inInterceptors());
        assertArrayEquals(new String[]{"outA"}, ann.outInterceptors());
        assertArrayEquals(new String[]{"faultIn"}, ann.inFaults());
        assertArrayEquals(new String[]{"faultOut"}, ann.outFaults());
        assertArrayEquals(new String[]{"featX", "featY", "featZ"}, ann.features());
        assertArrayEquals(new String[]{"h1", "h2"}, ann.handlers());
    }

    @Test
    public void shouldBeDiscoverableThroughReflectionApis() {
        Annotation[] all = FullyPopulatedSample.class.getAnnotations();
        boolean found = false;
        for (Annotation a : all) {
            if (a instanceof WebEndpoint) {
                found = true;
                assertEquals("http://example.com/svc", ((WebEndpoint) a).addr());
            }
        }
        assertTrue("Expected to find WebEndpoint in getAnnotations()", found);
    }

    @Test
    public void shouldReturnStableArraysAcrossRepeatedCalls() {
        WebEndpoint ann = FullyPopulatedSample.class.getAnnotation(WebEndpoint.class);
        String[] firstIns = ann.inInterceptors();
        String[] secondIns = ann.inInterceptors();
        assertArrayEquals(firstIns, secondIns);
        assertTrue(Arrays.equals(ann.features(), ann.features()));
        assertTrue(Arrays.equals(ann.handlers(), ann.handlers()));
    }

    @Test
    public void shouldReadTypeLevelWebBound() {
        WebBound ann = MethodLevelBoundSample.class.getAnnotation(WebBound.class);
        assertNotNull(ann);
        assertEquals("type-bound", ann.uid());
        assertEquals("{\"scope\":\"class\"}", ann.json());
    }

    @Test
    public void shouldReadMethodLevelWebBound() throws NoSuchMethodException {
        Method method = MethodLevelBoundSample.class.getMethod("sampleMethod");
        WebBound ann = method.getAnnotation(WebBound.class);
        assertNotNull(ann);
        assertEquals("method-bound", ann.uid());
        assertEquals("{\"scope\":\"method\"}", ann.json());
    }

    @Test
    public void shouldDefaultJsonWhenOnlyUidIsSupplied() throws NoSuchMethodException {
        Method method = MethodLevelBoundSample.class.getMethod("defaultsMethod");
        WebBound ann = method.getAnnotation(WebBound.class);
        assertNotNull(ann);
        assertEquals("only-uid-supplied", ann.uid());
        assertEquals("{}", ann.json());
    }

    @Test
    public void shouldDiscoverMethodLevelBoundViaEnumeration() throws NoSuchMethodException {
        Method method = MethodLevelBoundSample.class.getMethod("sampleMethod");
        boolean found = false;
        for (Annotation a : method.getAnnotations()) {
            if (a instanceof WebBound) {
                found = true;
                assertEquals("method-bound", ((WebBound) a).uid());
            }
        }
        assertTrue("Expected to find method-level WebBound", found);
    }
}
