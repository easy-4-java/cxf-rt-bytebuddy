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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Unit tests for {@link RestBound}.
 *
 * <p>Exercises both constructors and the getter/setter surface of the
 * {@code uid} and {@code json} fields.</p>
 *
 * @since 2.0.0
 */
public class RestBoundTest {

    /**
     * The single-argument constructor must propagate {@code uid} and
     * leave {@code json} at the default empty string.
     */
    @Test
    public void shouldBuildWithUidOnly() {
        RestBound bound = new RestBound("user-1");

        assertNotNull(bound);
        assertEquals("user-1", bound.getUid());
        assertEquals("", bound.getJson());
    }

    /**
     * The two-argument constructor must propagate both fields.
     */
    @Test
    public void shouldBuildWithUidAndJson() {
        RestBound bound = new RestBound("user-2", "{\"name\":\"Joe\"}");

        assertEquals("user-2", bound.getUid());
        assertEquals("{\"name\":\"Joe\"}", bound.getJson());
    }

    /**
     * Constructing with a {@code null} uid propagates {@code null} to the
     * uid field (overwriting the field default) while json retains its
     * default empty string.
     */
    @Test
    public void shouldPropagateNullUidAndDefaultJsonToEmptyString() {
        RestBound bound = new RestBound(null);

        assertNull(bound.getUid());
        assertEquals("", bound.getJson());
    }

    /**
     * Setters must update the values and the getters must reflect the
     * changes.
     */
    @Test
    public void shouldRoundTripValuesThroughSetters() {
        RestBound bound = new RestBound("a", "b");

        bound.setUid("42");
        bound.setJson("{\"x\":42}");

        assertEquals("42", bound.getUid());
        assertEquals("{\"x\":42}", bound.getJson());
    }

    /**
     * Setters must accept {@code null} without throwing.
     */
    @Test
    public void shouldAcceptNullThroughSetters() {
        RestBound bound = new RestBound("a", "b");

        bound.setUid(null);
        bound.setJson(null);

        assertEquals(null, bound.getUid());
        assertEquals(null, bound.getJson());
    }

    /**
     * The class should support repeated read/write cycles without
     * retaining the previous value.
     */
    @Test
    public void shouldOverwritePreviousValues() {
        RestBound bound = new RestBound("first", "{}");

        bound.setUid("second");
        bound.setJson("{\"k\":\"v\"}");
        bound.setUid("third");

        assertEquals("third", bound.getUid());
        assertEquals("{\"k\":\"v\"}", bound.getJson());
    }
}
