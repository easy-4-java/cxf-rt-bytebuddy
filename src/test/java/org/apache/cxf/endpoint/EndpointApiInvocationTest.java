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
package org.apache.cxf.endpoint;

import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

public class EndpointApiInvocationTest {

    private static final class SampleEndpoint extends EndpointApi {
        SampleEndpoint(InvocationHandler handler) {
            super(handler);
        }
    }

    private static final class CapturingHandler implements InvocationHandler {
        Object lastProxy;
        Method lastMethod;
        Object[] lastArgs;
        Object returnValue;
        Throwable toThrow;

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            lastProxy = proxy;
            lastMethod = method;
            lastArgs = args;
            if (toThrow != null) {
                throw toThrow;
            }
            return returnValue;
        }
    }

    @Test
    public void shouldForwardToStringThroughHandler() throws Throwable {
        CapturingHandler handler = new CapturingHandler();
        handler.returnValue = "captured-toString";
        SampleEndpoint endpoint = new SampleEndpoint(handler);

        Method toString = Object.class.getMethod("toString");
        Object result = handler.invoke(endpoint, toString, null);

        assertSame(endpoint, handler.lastProxy);
        assertSame(toString, handler.lastMethod);
        assertEquals("captured-toString", result);
    }

    @Test
    public void shouldForwardMethodWithArguments() throws Throwable {
        CapturingHandler handler = new CapturingHandler();
        handler.returnValue = 42;
        SampleEndpoint endpoint = new SampleEndpoint(handler);

        Method parseInt = Integer.class.getMethod("parseInt", String.class);
        Object[] args = new Object[]{"123"};
        Object result = handler.invoke(endpoint, parseInt, args);

        assertSame(args, handler.lastArgs);
        assertArrayEquals(new Object[]{"123"}, handler.lastArgs);
        assertEquals(42, result);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldPropagateHandlerExceptions() throws Throwable {
        CapturingHandler handler = new CapturingHandler();
        handler.toThrow = new IllegalStateException("boom");
        SampleEndpoint endpoint = new SampleEndpoint(handler);

        Method hashCode = Object.class.getMethod("hashCode");
        handler.invoke(endpoint, hashCode, null);
    }

    @Test
    public void shouldTolerateNullArgumentArray() throws Throwable {
        CapturingHandler handler = new CapturingHandler();
        handler.returnValue = "null-args-ok";
        SampleEndpoint endpoint = new SampleEndpoint(handler);

        Method m = Object.class.getMethod("hashCode");
        Object result = handler.invoke(endpoint, m, null);

        assertSame(endpoint, handler.lastProxy);
        assertSame(m, handler.lastMethod);
        assertEquals("null-args-ok", result);
    }

    @Test
    public void shouldInvokeHandlerExactlyOncePerForward() throws Throwable {
        final AtomicInteger counter = new AtomicInteger();
        InvocationHandler counting = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) {
                counter.incrementAndGet();
                return null;
            }
        };
        SampleEndpoint endpoint = new SampleEndpoint(counting);

        Method m = Object.class.getMethod("toString");
        counting.invoke(endpoint, m, null);
        counting.invoke(endpoint, m, null);

        assertEquals(2, counter.get());
    }

    @Test
    public void shouldNotLeakStateBetweenInstancesSharingHandler() throws Throwable {
        final AtomicReference<Object> lastProxy = new AtomicReference<Object>();
        InvocationHandler tracking = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) {
                lastProxy.set(proxy);
                return null;
            }
        };
        SampleEndpoint a = new SampleEndpoint(tracking);
        SampleEndpoint b = new SampleEndpoint(tracking);

        Method m = Object.class.getMethod("toString");
        tracking.invoke(a, m, null);
        assertSame(a, lastProxy.get());
        tracking.invoke(b, m, null);
        assertSame(b, lastProxy.get());
    }

    @Test
    public void shouldSupportInheritedObjectMethodsWithNullHandler() {
        SampleEndpoint endpoint = new SampleEndpoint(null);

        assertNotNull(endpoint.toString());
        int first = endpoint.hashCode();
        assertEquals(first, endpoint.hashCode());
    }
}
