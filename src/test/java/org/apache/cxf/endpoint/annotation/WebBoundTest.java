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
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link WebBound}.
 *
 * <p>Verifies the annotation's meta-attributes (target, retention,
 * inheritance, documentation) and the default values of the
 * {@code uid} and {@code json} attributes.</p>
 *
 * @since 3.0.0
 */
public class WebBoundTest {

    /**
     * Holder type with an empty {@link WebBound} annotation used to
     * exercise the default values.
     */
    @WebBound
    private static final class DefaultBound {
        // holder only
    }

    /**
     * Holder type with explicit attribute values.
     */
    @WebBound(uid = "user-42", json = "{\"role\":\"admin\"}")
    private static final class ExplicitBound {
        // holder only
    }

    /**
     * The annotation must be retained at runtime.
     */
    @Test
    public void shouldBeRetainedAtRuntime() {
        Retention retention = WebBound.class.getAnnotation(Retention.class);
        assertNotNull(retention);
        assertEquals(RetentionPolicy.RUNTIME, retention.value());
    }

    /**
     * The annotation must be applicable to both types and methods.
     */
    @Test
    public void shouldTargetTypesAndMethods() {
        Target target = WebBound.class.getAnnotation(Target.class);
        assertNotNull(target);
        assertArrayEquals(
                new ElementType[]{ElementType.TYPE, ElementType.METHOD},
                target.value());
    }

    /**
     * The annotation must be documented.
     */
    @Test
    public void shouldBeDocumented() {
        assertNotNull(WebBound.class.getAnnotation(Documented.class));
    }

    /**
     * The annotation must be inheritable so subclasses can carry the
     * binding forward.
     */
    @Test
    public void shouldBeInherited() {
        assertNotNull(WebBound.class.getAnnotation(Inherited.class));
    }

    /**
     * Empty annotations must default {@code uid} to {@code ""}.
     */
    @Test
    public void shouldDefaultUidToEmptyString() {
        WebBound annotation = DefaultBound.class.getAnnotation(WebBound.class);
        assertNotNull(annotation);
        assertEquals("", annotation.uid());
    }

    /**
     * Empty annotations must default {@code json} to {@code "{}"}.
     */
    @Test
    public void shouldDefaultJsonToEmptyObject() {
        WebBound annotation = DefaultBound.class.getAnnotation(WebBound.class);
        assertNotNull(annotation);
        assertEquals("{}", annotation.json());
    }

    /**
     * Explicit attribute values must round-trip through reflection.
     */
    @Test
    public void shouldReflectExplicitAttributes() {
        WebBound annotation = ExplicitBound.class.getAnnotation(WebBound.class);
        assertNotNull(annotation);
        assertEquals("user-42", annotation.uid());
        assertEquals("{\"role\":\"admin\"}", annotation.json());
    }

    /**
     * The annotation must be discoverable on the annotated holder.
     */
    @Test
    public void shouldBeDiscoverableOnHolder() {
        Annotation[] annotations = ExplicitBound.class.getAnnotations();
        assertTrue("at least one annotation must be present",
                Arrays.stream(annotations).anyMatch(a -> a instanceof WebBound));
    }
}
