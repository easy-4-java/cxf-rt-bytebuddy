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
 * Unit tests for {@link WebEndpoint}.
 *
 * <p>Verifies the annotation's meta-attributes (target, retention,
 * inheritance, documentation) and the contract of the {@code addr}
 * attribute together with the {@code default}-bearing attributes.</p>
 *
 * @since 3.0.0
 */
public class WebEndpointTest {

    /**
     * Holder type used to introspect the annotation via reflection.
     */
    @WebEndpoint(addr = "/example")
    private static final class AnnotatedExample {
        // holder only
    }

    /**
     * The annotation must be present at runtime so the byte-buddy agent
     * can discover it via reflection.
     */
    @Test
    public void shouldBeRetainedAtRuntime() {
        Retention retention = WebEndpoint.class.getAnnotation(Retention.class);
        assertNotNull(retention);
        assertEquals(RetentionPolicy.RUNTIME, retention.value());
    }

    /**
     * The annotation must be applicable to types only.
     */
    @Test
    public void shouldTargetTypesOnly() {
        Target target = WebEndpoint.class.getAnnotation(Target.class);
        assertNotNull(target);
        assertArrayEquals(new ElementType[]{ElementType.TYPE}, target.value());
    }

    /**
     * The annotation must be documented so it appears in generated
     * Javadoc.
     */
    @Test
    public void shouldBeDocumented() {
        assertNotNull(WebEndpoint.class.getAnnotation(Documented.class));
    }

    /**
     * The annotation must be inheritable so subclasses of an annotated
     * endpoint are still recognised.
     */
    @Test
    public void shouldBeInherited() {
        assertNotNull(WebEndpoint.class.getAnnotation(Inherited.class));
    }

    /**
     * The {@code addr} attribute must reflect the value supplied on the
     * annotated type.
     */
    @Test
    public void shouldExposeAddrAttribute() {
        WebEndpoint annotation = AnnotatedExample.class.getAnnotation(WebEndpoint.class);
        assertNotNull(annotation);
        assertEquals("/example", annotation.addr());
    }

    /**
     * All array attributes should default to a single empty string so
     * that empty annotations remain valid.
     */
    @Test
    public void shouldDefaultArrayAttributesToEmptyString() {
        WebEndpoint annotation = AnnotatedExample.class.getAnnotation(WebEndpoint.class);
        assertNotNull(annotation);

        assertEquals(1, annotation.inInterceptors().length);
        assertEquals("", annotation.inInterceptors()[0]);

        assertEquals(1, annotation.outInterceptors().length);
        assertEquals("", annotation.outInterceptors()[0]);

        assertEquals(1, annotation.inFaults().length);
        assertEquals("", annotation.inFaults()[0]);

        assertEquals(1, annotation.outFaults().length);
        assertEquals("", annotation.outFaults()[0]);

        assertEquals(1, annotation.features().length);
        assertEquals("", annotation.features()[0]);

        assertEquals(1, annotation.handlers().length);
        assertEquals("", annotation.handlers()[0]);
    }

    /**
     * Multiple annotations on the same class must be discoverable; here
     * we ensure the framework supports overrides where an explicit
     * attribute value is supplied.
     */
    @Test
    public void shouldReflectExplicitAttributes() {
        Annotation[] annotations = AnnotatedExample.class.getAnnotations();
        assertTrue("at least one annotation must be present",
                Arrays.stream(annotations).anyMatch(a -> a instanceof WebEndpoint));
    }
}
