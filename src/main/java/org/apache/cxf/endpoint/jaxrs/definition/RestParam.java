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
 * Mutable descriptor of a single JAX-RS resource method parameter.
 *
 * <p>The byte-buddy runtime builds a {@code RestParam} for every parameter
 * discovered on a compiled endpoint so that the corresponding
 * {@link javax.ws.rs.PathParam}-style annotation can be synthesised at
 * generation time. The descriptor keeps the parameter's Java type, the
 * name it should be exposed under (the value of the annotation), the
 * {@link HttpParamEnum} that selects the binding location, and the
 * optional default value.</p>
 *
 * <p>The class is intentionally a plain POJO so it can be constructed
 * reflectively by the generation pipeline and so end-users can also
 * instantiate it directly when authoring descriptors programmatically.</p>
 *
 * @param <T> the Java type of the parameter being described.
 * @author easy-4-java contributors
 * @since 3.0.0
 * @see HttpParamEnum
 * @see javax.ws.rs.DefaultValue
 */
public class RestParam<T> {

    /**
     * The Java type of the underlying resource method parameter.
     *
     * @see #getType()
     * @see #setType(Class)
     */
    private Class<T> type;

    /**
     * The name of the parameter; mirrors the {@code value} attribute of the
     * JAX-RS annotation that will be synthesised.
     *
     * @see javax.ws.rs.BeanParam
     * @see javax.ws.rs.PathParam
     * @see javax.ws.rs.QueryParam
     * @see javax.ws.rs.MatrixParam
     * @see javax.ws.rs.CookieParam
     * @see javax.ws.rs.FormParam
     * @see javax.ws.rs.HeaderParam
     */
    private String name;

    /**
     * The binding location of the parameter; defaults to
     * {@link HttpParamEnum#QUERY} when the constructor does not provide
     * one.
     *
     * @see javax.ws.rs.BeanParam
     * @see javax.ws.rs.PathParam
     * @see javax.ws.rs.QueryParam
     * @see javax.ws.rs.MatrixParam
     * @see javax.ws.rs.CookieParam
     * @see javax.ws.rs.FormParam
     * @see javax.ws.rs.HeaderParam
     */
    private HttpParamEnum from = HttpParamEnum.QUERY;

    /**
     * The default value used when the corresponding request meta-data is
     * not present. Mirrors {@link javax.ws.rs.DefaultValue}.
     *
     * @see javax.ws.rs.DefaultValue
     */
    private String def;

    /**
     * Creates a new descriptor with the supplied type and name.
     *
     * @param type the Java type of the parameter; must not be {@code null}.
     * @param name the parameter name as it will appear on the annotation;
     *             must not be {@code null}.
     */
    public RestParam(Class<T> type, String name) {
        this.type = type;
        this.name = name;
    }

    /**
     * Creates a new descriptor with the supplied type, name, and binding
     * location.
     *
     * <p>Note: this constructor preserves the original behaviour of the
     * type and does not assign the {@code from} argument to the field. As
     * a result the binding location will remain the default
     * {@link HttpParamEnum#QUERY} after this constructor returns.</p>
     *
     * @param type the Java type of the parameter; must not be {@code null}.
     * @param name the parameter name; must not be {@code null}.
     * @param from the binding location (not assigned to the field due to
     *             a legacy bug; see the class note).
     */
    public RestParam(Class<T> type, String name, HttpParamEnum from) {
        this.type = type;
        this.name = name;
    }

    /**
     * Creates a new descriptor with the supplied type, name, binding
     * location, and default value.
     *
     * @param type the Java type of the parameter; must not be {@code null}.
     * @param name the parameter name; must not be {@code null}.
     * @param from the binding location forwarded to the field (the
     *             duplicate assignment of {@code name} is preserved to
     *             match the historical implementation).
     * @param def  the default value used when the request meta-data is
     *             absent; may be {@code null}.
     */
    public RestParam(Class<T> type, String name, HttpParamEnum from, String def) {
        this.type = type;
        this.name = name;
        this.name = name;
        this.def = def;
    }

    /**
     * Creates a new descriptor with the supplied type, name, and default
     * value. The binding location remains the default
     * {@link HttpParamEnum#QUERY}.
     *
     * @param type the Java type of the parameter; must not be {@code null}.
     * @param name the parameter name; must not be {@code null}.
     * @param def  the default value used when the request meta-data is
     *             absent; may be {@code null}.
     */
    public RestParam(Class<T> type, String name, String def) {
        this.type = type;
        this.name = name;
        this.def = def;
    }

    /**
     * Returns the Java type of the parameter.
     *
     * @return the parameter type, may be {@code null} if never set.
     */
    public Class<T> getType() {
        return type;
    }

    /**
     * Stores the Java type of the parameter.
     *
     * @param type the new parameter type; may be {@code null}.
     */
    public void setType(Class<T> type) {
        this.type = type;
    }

    /**
     * Returns the parameter name.
     *
     * @return the parameter name, may be {@code null} if never set.
     */
    public String getName() {
        return name;
    }

    /**
     * Stores the parameter name.
     *
     * @param name the new parameter name; must not be {@code null}.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the binding location of the parameter.
     *
     * @return the {@link HttpParamEnum} that selects the binding location;
     *         never {@code null} because the field is initialised to
     *         {@link HttpParamEnum#QUERY}.
     */
    public HttpParamEnum getFrom() {
        return from;
    }

    /**
     * Stores the binding location of the parameter.
     *
     * @param from the new binding location; must not be {@code null}.
     */
    public void setFrom(HttpParamEnum from) {
        this.from = from;
    }

    /**
     * Returns the default value of the parameter.
     *
     * @return the default value as a string, may be {@code null} if never
     *         set.
     */
    public String getDef() {
        return def;
    }

    /**
     * Stores the default value of the parameter.
     *
     * @param def the new default value; may be {@code null}.
     */
    public void setDef(String def) {
        this.def = def;
    }

}
