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

import java.lang.reflect.InvocationHandler;

/**
 * Abstract base class for dynamically generated CXF endpoint APIs.
 *
 * <p>The byte-buddy runtime produces concrete implementations of this class at
 * build time to expose JAX-WS or JAX-RS style endpoints without requiring
 * hand-written service classes. Each generated subclass carries an
 * {@link InvocationHandler} that is responsible for translating incoming
 * endpoint invocations into the underlying delegate, while the base class
 * itself only stores the handler and exposes the accessor used by the
 * generated dispatch code.</p>
 *
 * <p>Subclasses are produced by the byte-buddy agent and are not meant to be
 * instantiated directly by application code. The two constructors allow the
 * generation framework to either create a stub for later handler injection
 * (the no-argument form) or wire up the handler in a single step (the
 * argument-taking form).</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 * @see java.lang.reflect.InvocationHandler
 * @see java.lang.reflect.Proxy
 */
public abstract class EndpointApi {

    /**
     * The {@link InvocationHandler} that backs every dispatch through the
     * generated subclass. May be {@code null} when the no-argument
     * constructor is used and the handler has not been attached yet.
     */
    private InvocationHandler handler;

    /**
     * Creates a new instance without a backing handler.
     *
     * <p>Used by the byte-buddy generation pipeline when the handler will be
     * attached later, or by tests that need a concrete subclass to verify
     * the dispatch contract.</p>
     */
    public EndpointApi() {
    }

    /**
     * Creates a new instance with the supplied handler bound to it.
     *
     * @param handler the {@link InvocationHandler} that will receive every
     *                method invocation dispatched through the generated
     *                subclass; may be {@code null} if the subclass is
     *                expected to attach a handler lazily.
     */
    public EndpointApi(InvocationHandler handler) {
        this.handler = handler;
    }

    /**
     * Returns the {@link InvocationHandler} associated with this endpoint.
     *
     * @return the handler bound to this instance, or {@code null} if no
     *         handler has been attached yet.
     */
    public InvocationHandler getHandler() {
        return handler;
    }


}
