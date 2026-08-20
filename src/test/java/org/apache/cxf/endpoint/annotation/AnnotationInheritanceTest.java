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

import org.apache.cxf.endpoint.EndpointApi;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class AnnotationInheritanceTest {

    @WebEndpoint(
            addr = "http://example.com/inherited-svc",
            inInterceptors = {"logIn"},
            outInterceptors = {"logOut"},
            features = {"gzip"})
    @WebBound(uid = "parent", json = "{\"parent\":true}")
    private abstract static class AnnotatedBaseApi extends EndpointApi {
        protected AnnotatedBaseApi() { super(); }
        protected AnnotatedBaseApi(InvocationHandler h) { super(h); }
    }

    private static final class UnannotatedChildApi extends AnnotatedBaseApi {
        UnannotatedChildApi(InvocationHandler h) { super(h); }
    }

    @WebEndpoint(addr = "http://child.example.com/override",
            handlers = {"childHandler"})
    private static final class OverridingChildApi extends AnnotatedBaseApi {
    }

    @WebBound(uid = "child-uid")
    private static final class OverridingBoundChildApi extends AnnotatedBaseApi {
    }

    @Test
    public void shouldInheritWebEndpointFromParent() {
        WebEndpoint ann = UnannotatedChildApi.class.getAnnotation(WebEndpoint.class);
        assertNotNull("@WebEndpoint must be inherited", ann);
        assertEquals("http://example.com/inherited-svc", ann.addr());
        assertArrayEquals(new String[]{"logIn"}, ann.inInterceptors());
        assertArrayEquals(new String[]{"logOut"}, ann.outInterceptors());
        assertArrayEquals(new String[]{"gzip"}, ann.features());
        assertArrayEquals(new String[]{""}, ann.handlers());
    }

    @Test
    public void shouldInheritWebBoundFromParent() {
        WebBound bound = UnannotatedChildApi.class.getAnnotation(WebBound.class);
        assertNotNull("@WebBound must be inherited (marked @Inherited)", bound);
        assertEquals("parent", bound.uid());
        assertEquals("{\"parent\":true}", bound.json());
    }

    @Test
    public void shouldReplaceInheritedWebBoundWhenChildRedefinesIt() {
        WebBound bound = OverridingBoundChildApi.class.getAnnotation(WebBound.class);
        assertNotNull(bound);
        assertEquals("child-uid", bound.uid());
        assertEquals("{}", bound.json());
    }

    @Test
    public void shouldReplaceInheritedWebEndpointWhenChildRedefinesIt() {
        WebEndpoint ann = OverridingChildApi.class.getAnnotation(WebEndpoint.class);
        assertNotNull(ann);
        assertEquals("http://child.example.com/override", ann.addr());
        assertArrayEquals(new String[]{"childHandler"}, ann.handlers());
        assertArrayEquals(new String[]{""}, ann.features());
        assertArrayEquals(new String[]{""}, ann.inInterceptors());
        assertArrayEquals(new String[]{""}, ann.outInterceptors());
    }

    @Test
    public void shouldReadAnnotationsFromConcreteEndpointApiInstance() {
        UnannotatedChildApi endpoint = new UnannotatedChildApi(null);
        Class<?> clazz = endpoint.getClass();
        WebEndpoint endpointAnn = clazz.getAnnotation(WebEndpoint.class);
        assertNotNull(endpointAnn);
        assertEquals("http://example.com/inherited-svc", endpointAnn.addr());
        assertSame(null, endpoint.getHandler());
    }

    @Test
    public void shouldPreserveAnnotationsEvenWhenHandlerIsSet() {
        final InvocationHandler noop = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, java.lang.reflect.Method method, Object[] args) {
                return null;
            }
        };
        UnannotatedChildApi endpoint = new UnannotatedChildApi(noop);
        WebEndpoint ann = endpoint.getClass().getAnnotation(WebEndpoint.class);
        assertNotNull(ann);
        assertSame(noop, endpoint.getHandler());
    }

    @Test
    public void shouldReportInheritedAnnotationPresentOnChild() {
        assertTrue(UnannotatedChildApi.class.isAnnotationPresent(WebEndpoint.class));
    }
}
