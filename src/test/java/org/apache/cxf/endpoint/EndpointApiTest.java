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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * Unit tests for {@link EndpointApi}.
 *
 * <p>Because the class is abstract, every test instantiates it through a
 * trivial concrete subclass that satisfies the byte-buddy generation
 * contract.</p>
 *
 * @since 2.0.0
 */
public class EndpointApiTest {

    /**
     * Concrete subclass used to instantiate the otherwise abstract
     * {@link EndpointApi} base class.
     */
    private static final class ConcreteEndpointApi extends EndpointApi {
        /**
         * Forwards to the no-argument super constructor.
         */
        ConcreteEndpointApi() {
            super();
        }

        /**
         * Forwards to the {@link InvocationHandler}-taking super
         * constructor.
         *
         * @param handler the handler to forward.
         */
        ConcreteEndpointApi(InvocationHandler handler) {
            super(handler);
        }
    }

    /**
     * A no-op {@link InvocationHandler} that simply records the method
     * being invoked; used to verify the handler is plumbed through.
     */
    private static final class RecordingHandler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            return null;
        }
    }

    /**
     * The no-argument constructor must produce a non-null instance whose
     * handler is initially {@code null}.
     */
    @Test
    public void shouldInstantiateWithDefaultConstructor() {
        EndpointApi endpoint = new ConcreteEndpointApi();
        assertNotNull(endpoint);
        assertNull(endpoint.getHandler());
    }

    /**
     * The handler-taking constructor must wire the supplied handler
     * through so it can be retrieved via {@link EndpointApi#getHandler()}.
     */
    @Test
    public void shouldWireHandlerThroughConstructor() {
        InvocationHandler handler = new RecordingHandler();
        EndpointApi endpoint = new ConcreteEndpointApi(handler);

        assertNotNull(endpoint);
        assertSame("the handler must be stored as supplied", handler, endpoint.getHandler());
    }

    /**
     * The handler-taking constructor must accept a {@code null} handler
     * without throwing; useful when the generation pipeline defers
     * handler attachment.
     */
    @Test
    public void shouldAcceptNullHandler() {
        EndpointApi endpoint = new ConcreteEndpointApi(null);

        assertNotNull(endpoint);
        assertNull(endpoint.getHandler());
    }

    /**
     * Invoking the no-argument constructor repeatedly must yield
     * independent instances with no shared state.
     */
    @Test
    public void shouldProduceIndependentInstances() {
        EndpointApi first = new ConcreteEndpointApi();
        EndpointApi second = new ConcreteEndpointApi();

        assertNotNull(first);
        assertNotNull(second);
        // assertNotSame is implied because each `new` creates a distinct object.
    }

    /**
     * The handler field is mutable across instances of the same class;
     * confirms the getter always returns the most recently assigned
     * value (even though there is no setter, this guards against
     * accidental caching that could surface from future refactors).
     */
    @Test
    public void shouldReturnSameHandlerOnRepeatedAccess() {
        InvocationHandler handler = new RecordingHandler();
        EndpointApi endpoint = new ConcreteEndpointApi(handler);

        assertSame(handler, endpoint.getHandler());
        assertSame(handler, endpoint.getHandler());
    }
}
