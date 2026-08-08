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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit tests for {@link RestProduce}.
 *
 * <p>Exercises the constructor and the getter/setter surface of the
 * mutable media-types field.</p>
 *
 * @since 2.0.0
 */
public class RestProduceTest {

    /**
     * The constructor must propagate the URI path and the supplied
     * media types verbatim.
     */
    @Test
    public void shouldBuildWithPathAndMediaTypes() {
        RestProduce produce = new RestProduce("/items", "application/json", "application/xml");

        assertEquals("/items", produce.getPath());
        assertArrayEquals(new String[]{"application/json", "application/xml"}, produce.getMediaTypes());
    }

    /**
     * The path field is {@code final}, so the getter must always
     * expose the value supplied to the constructor.
     */
    @Test
    public void shouldExposeFinalPath() {
        RestProduce produce = new RestProduce("/v1/users", "application/json");

        assertEquals("/v1/users", produce.getPath());
    }

    /**
     * The {@code mediaTypes} setter must overwrite the previously
     * stored array.
     */
    @Test
    public void shouldOverrideMediaTypes() {
        RestProduce produce = new RestProduce("/items", "application/json");
        String[] mediaTypes = new String[]{"text/plain", "text/xml"};
        produce.setMediaTypes(mediaTypes);

        assertArrayEquals(mediaTypes, produce.getMediaTypes());
    }

    /**
     * Calling the constructor with no media types must leave the field
     * as an empty array.
     */
    @Test
    public void shouldAcceptEmptyMediaTypes() {
        RestProduce produce = new RestProduce("/items");

        assertNotNull(produce.getMediaTypes());
        assertEquals(0, produce.getMediaTypes().length);
        assertEquals("/items", produce.getPath());
    }

    /**
     * The {@code mediaTypes} setter must accept a single media type
     * without wrapping it in a list.
     */
    @Test
    public void shouldAcceptSingleMediaTypeViaSetter() {
        RestProduce produce = new RestProduce("/items");
        produce.setMediaTypes(new String[]{"application/json"});

        assertEquals(1, produce.getMediaTypes().length);
        assertEquals("application/json", produce.getMediaTypes()[0]);
    }

    /**
     * The descriptor must survive repeated read/write cycles.
     */
    @Test
    public void shouldOverwritePreviousMediaTypes() {
        RestProduce produce = new RestProduce("/items", "application/json");
        produce.setMediaTypes(new String[]{"text/plain"});
        produce.setMediaTypes(new String[]{"application/xml"});

        assertEquals("application/xml", produce.getMediaTypes()[0]);
    }
}
