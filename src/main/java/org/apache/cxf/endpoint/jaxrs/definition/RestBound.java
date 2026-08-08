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

/**
 * Mutable value object that transports the data bound to a method or type
 * via the {@link org.apache.cxf.endpoint.annotation.WebBound} annotation.
 *
 * <p>The instance carries two correlated pieces of information:</p>
 * <ul>
 *   <li>{@link #uid} &mdash; a logical identifier, typically a primary
 *       key, propagated alongside the binding so that the implementation
 *       can perform data lookups.</li>
 *   <li>{@link #json} &mdash; a JSON encoded payload that the runtime
 *       will marshal into the bound object. JSON is used as the wire
 *       format to keep the binding language neutral.</li>
 * </ul>
 *
 * <p>Both fields default to an empty string so that the helper can be
 * instantiated before the dispatching layer has had a chance to populate
 * it.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see org.apache.cxf.endpoint.annotation.WebBound
 */
public class RestBound {

    /**
     * Builds a new instance with the supplied identifier.
     *
     * @param uid the binding identifier; may be {@code null} but is
     *            normalised to an empty string by the field default.
     */
    public RestBound(String uid) {
        this.uid = uid;
    }

    /**
     * Builds a new instance with the supplied identifier and JSON payload.
     *
     * @param uid  the binding identifier; may be {@code null}.
     * @param json the JSON encoded payload; may be {@code null}.
     */
    public RestBound(String uid, String json) {
        this.uid = uid;
        this.json = json;
    }

    /**
     * Identifier (typically a primary key) that the implementation can
     * use to locate additional data. Defaults to an empty string.
     */
    private String uid = "";

    /**
     * JSON encoded payload that the runtime will marshal into the bound
     * object. Defaults to an empty string.
     */
    private String json = "";

    /**
     * Returns the binding identifier.
     *
     * @return the identifier, never {@code null} (defaults to an empty
     *         string).
     */
    public String getUid() {
        return uid;
    }

    /**
     * Stores the binding identifier.
     *
     * @param uid the new identifier; may be {@code null}.
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * Returns the JSON encoded payload.
     *
     * @return the JSON payload, never {@code null} (defaults to an empty
     *         string).
     */
    public String getJson() {
        return json;
    }

    /**
     * Stores the JSON encoded payload.
     *
     * @param json the new JSON payload; may be {@code null}.
     */
    public void setJson(String json) {
        this.json = json;
    }

}

