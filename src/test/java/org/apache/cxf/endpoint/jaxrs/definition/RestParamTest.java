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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * Unit tests for {@link RestParam}.
 *
 * <p>Exercises every constructor and accessor, including the legacy
 * behaviour where the three-argument constructor does not propagate the
 * {@code from} argument.</p>
 *
 * @since 1.0.0
 */
public class RestParamTest {

    /**
     * The two-argument constructor must propagate {@code type} and
     * {@code name} and leave {@code from} at the default
     * {@link HttpParamEnum#QUERY}; {@code def} must remain {@code null}.
     */
    @Test
    public void shouldExposeTypeAndNameFromTwoArgConstructor() {
        RestParam<String> param = new RestParam<>(String.class, "id");

        assertSame(String.class, param.getType());
        assertEquals("id", param.getName());
        assertSame(HttpParamEnum.QUERY, param.getFrom());
        assertNull(param.getDef());
    }

    /**
     * The three-argument constructor must record {@code type} and
     * {@code name} but, due to the historical bug, must leave
     * {@code from} at the default {@link HttpParamEnum#QUERY}.
     */
    @Test
    public void shouldPreserveLegacyBehaviourForThreeArgConstructor() {
        RestParam<Integer> param = new RestParam<>(Integer.class, "count", HttpParamEnum.HEADER);

        assertSame(Integer.class, param.getType());
        assertEquals("count", param.getName());
        assertSame("three-arg ctor must NOT assign from", HttpParamEnum.QUERY, param.getFrom());
        assertNull(param.getDef());
    }

    /**
     * The four-argument constructor stores type, name, and def, but due
     * to a legacy bug does not assign the {@code from} argument; the
     * binding location remains the default {@link HttpParamEnum#QUERY}.
     */
    @Test
    public void shouldBuildWithAllFourAttributes() {
        RestParam<Long> param = new RestParam<>(Long.class, "size", HttpParamEnum.PATH, "0");

        assertSame(Long.class, param.getType());
        assertEquals("size", param.getName());
        assertSame("four-arg ctor must NOT assign from", HttpParamEnum.QUERY, param.getFrom());
        assertEquals("0", param.getDef());
    }

    /**
     * The (type, name, def) constructor must keep {@code from} at the
     * default {@link HttpParamEnum#QUERY}.
     */
    @Test
    public void shouldBuildWithTypeNameAndDefault() {
        RestParam<String> param = new RestParam<>(String.class, "search", "hello");

        assertSame(String.class, param.getType());
        assertEquals("search", param.getName());
        assertSame(HttpParamEnum.QUERY, param.getFrom());
        assertEquals("hello", param.getDef());
    }

    /**
     * All setters must round-trip the values they receive.
     */
    @Test
    public void shouldRoundTripValuesThroughSetters() {
        RestParam<String> param = new RestParam<>(String.class, "placeholder");

        param.setType(String.class);
        param.setName("amount");
        param.setFrom(HttpParamEnum.FORM);
        param.setDef("1");

        assertSame(String.class, param.getType());
        assertEquals("amount", param.getName());
        assertSame(HttpParamEnum.FORM, param.getFrom());
        assertEquals("1", param.getDef());
    }

    /**
     * Setters must accept {@code null} values without throwing.
     */
    @Test
    public void shouldAcceptNullThroughSetters() {
        RestParam<String> param = new RestParam<>(String.class, "x");

        param.setType(null);
        param.setName(null);
        param.setFrom(null);
        param.setDef(null);

        assertNull(param.getType());
        assertNull(param.getName());
        assertNull(param.getFrom());
        assertNull(param.getDef());
    }

    /**
     * The default {@code from} field must be {@link HttpParamEnum#QUERY}
     * for every constructor that does not assign it.
     */
    @Test
    public void shouldDefaultFromToQuery() {
        assertSame(HttpParamEnum.QUERY, new RestParam<>(String.class, "a").getFrom());
        assertSame(HttpParamEnum.QUERY, new RestParam<>(String.class, "b", "d").getFrom());
    }
}
