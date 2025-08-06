package com.manchickas.john.ast;

import com.manchickas.john.John;
import com.manchickas.john.exception.JsonException;
import com.manchickas.john.path.JsonPath;
import com.manchickas.john.position.SourceSpan;
import com.manchickas.john.template.Template;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a single node in a JSON structure.
 * <br><br>
 * Each {@link JsonElement} carries its {@link SourceSpan} that mirrors its
 * position in the original source string it was parsed from. When an element is created
 * programmatically, its {@link SourceSpan} is set to {@code null}.
 * <br><br>
 * The {@link JsonElement} doesn't provide enough meaningful ways to introspect the underlying
 * element and its structure. Instead, an excessive usage of {@link Template}s is assumed.
 */
public abstract class JsonElement {

    @Nullable
    protected final SourceSpan span;

    protected JsonElement(@Nullable SourceSpan span) {
        this.span = span;
    }

    /**
     * Attempts to parse the current {@link JsonElement} according the provided {@link Template}.
     *
     * @param template the {@link Template} this element <b>must</b> satisfy.
     * @return the parsed from the current element value.
     * @throws JsonException if the element doesn't satisfy the provided {@link Template}.
     */
    public <T> T expect(Template<T> template) throws JsonException {
        var result = template.parseAndPromote(this);
        if (result.isSuccess())
            return result.unwrap();
        throw new JsonException(result.message())
                .withSpan(result.span());
    }

    /**
     * Attempts to access the element at the provided path, relative to the current element.
     *
     * @param path the path to traverse.
     * @return the {@link JsonElement} at the provided path.
     * @throws JsonException if the JSON structure doesn't match the one expected by the path.
     */
    @NotNull
    public JsonElement get(String path) throws JsonException {
        return this.get(JsonPath.compile(path));
    }

    /**
     * Attempts to access the element at the provided precompiled {@link JsonPath}, relative to the current element.
     *
     * @param path the path to traverse.
     * @return the {@link JsonElement} at the provided path.
     * @throws JsonException if the JSON structure doesn't match the one expected by the path.
     */
    @NotNull
    public JsonElement get(JsonPath path) throws JsonException {
        return path.traverse(this);
    }

    /**
     * Attempts to parse the element at the provided path according to the provided {@link JsonElement}.
     * <br><br>
     * <b>Note,</b> the whole family of {@code get} methods <b>never</b> returns {@code null} as a result of unsuccessful retrieval. The only case in which a {@code null} may be returned,
     * and the only reason the <b>template-overloaded</b> methods aren't annotated with {@link NotNull @NotNull} is when a {@link Template#NULL} is explicitly requested.
     *
     * @param path     the path to traverse.
     * @param template the {@link Template} the element at the provided path must satisfy.
     * @return the parsed from the {@link JsonElement} value.
     * @throws JsonException if the JSON structure doesn't match the one expected by the path, or the retrieved {@link JsonElement} doesn't satisfy the provided {@link Template}.
     *
     */
    public <T> T get(String path, Template<T> template) throws JsonException {
        return this.get(JsonPath.compile(path), template);
    }

    /**
     * Attempts to parse the element at the provided precompiled {@link JsonPath} according to the provided {@link JsonElement}.
     * <br><br>
     * <b>Note,</b> the whole family of {@code get} methods <b>never</b> returns {@code null} as a result of unsuccessful retrieval. The only case in which a {@code null} may be returned,
     * and the only reason the <b>template-overloaded</b> methods aren't annotated with {@link NotNull @NotNull} is when a {@link Template#NULL} is explicitly requested.
     *
     * @param path     the path to traverse.
     * @param template the {@link Template} the element at the provided path must satisfy.
     * @return the parsed from the {@link JsonElement} value.
     * @throws JsonException if the JSON structure doesn't match the one expected by the path, or the retrieved {@link JsonElement} doesn't satisfy the provided {@link Template}.
     */
    public <T> T get(JsonPath path, Template<T> template) throws JsonException {
        return this.get(path).expect(template);
    }

    /**
     * Attempts to retrieve the provided property from the {@link JsonElement}.
     * <br><br>
     * <b>Note,</b> while the {@code property} method may be used publicly, it's intended for
     *  internal usage by {@link JsonPath}s. The family of {@link #get(String) get} methods
     *  should be preferred instead.
     *
     * @param name the property to retrieve.
     * @return the value of the provided property.
     * @throws JsonException if the current {@link JsonElement} doesn't represent an object,
     *                       or the property is missing on the object.
     */
    @NotNull
    @ApiStatus.Internal
    public JsonElement property(String name) throws JsonException {
        throw new JsonException("Expected an object.")
                .withSpan(this.span);
    }

    /**
     * Attempts to retrieve the provided index from the {@link JsonElement}.
     * <br><br>
     *
     * <b>Note,</b> while the {@code subscript} method may be used publicly, it's intended for
     * internal usage by {@link JsonPath}s. The family of {@link #get(String) get} methods
     * should be preferred instead.
     *
     * @param index the index to retrieve.
     * @return the value at the provided index.
     * @throws JsonException if the current {@link JsonElement} doesn't represent an object,
     *                       a negative index was provided, or the array contains too few elements.
     */
    @NotNull
    @ApiStatus.Internal
    public JsonElement subscript(int index) throws JsonException {
        throw new JsonException("Expected an array.")
                .withSpan(this.span);
    }

    /**
     * Serializes the {@link JsonElement} into a specially formatted string, that,
     * when processed, can easily be turned into the JSON representation by following a set of simple
     * sequential substitutions.
     * <br><br>
     * The pattern is used internally by the {@link John#stringifyPattern(String, int)} method,
     * and it's thus referred to as the <b>stringify pattern</b>.
     * <br><br>
     *
     * @return the stringify pattern for the {@link JsonElement}
     * @see John#stringify(JsonElement, int)
     */
    public abstract String stringifyPattern();

    /**
     * Returns the {@code length} of the element.
     * <br><br>
     * The <i>length</i> of the {@link JsonElement} is an abstract concept, defined by its specific implementation.
     * <br>
     * <table>
     *     <caption>Method Implementations</caption>
     *     <tr>
     *         <th>Subclass</th>
     *         <th>Length</th>
     *     </tr>
     *     <tr>
     *         <td>{@link JsonObject}</td>
     *         <td>The number of entries in the object.</td>
     *     </tr>
     *     <tr>
     *         <td>{@link JsonArray}</td>
     *         <td>The number of elements in the array.</td>
     *     </tr>
     *     <tr>
     *         <td>{@link com.manchickas.john.ast.primitive.JsonPrimitive JsonPrimitive}</td>
     *         <td>Always {@code 1}, regardless of the input.</td>
     *     </tr>
     * </table>
     *
     * @return the length of the element.
     */
    public abstract int length();

    /**
     * Determines whether the provided {@code obj} equals the current {@link JsonElement}
     * semantically.
     * <br><br>
     * The {@link JsonElement} is considered equal to the provided {@code obj} under the following conditions:
     * <ul>
     *     <li><b>IF</b> The provided {@code obj} has the identity of the {@link JsonElement}, <b>THEN</b> the two objects are considered equal.</li>
     *     <li><b>OR</b> Both objects represent {@link JsonElement}s of the <b>same subtype</b>, <b>THEN</b>:
     *          <ul>
     *              <li><b>IF</b> One of the elements doesn't have a {@link SourceSpan} attached to it, <b>OR</b>
     *              the two {@link SourceSpan}s are semantically equal, <b>THEN</b>:
     *                  <ul>
     *                      <li>
     *                          <b>IF</b> The underlying values of the two objects are semantically equal, <B>THEN</B> the two objects are considered equal.
     *                      </li>
     *                  </ul>
     *              </li>
     *          </ul>
     *     </li>
     * </ul>
     *
     * @param obj the {@link Object} to compare against.
     * @return {@code true} if the {@link JsonElement} is semantically equal to the provided {@code obj}, {@code false} otherwise.
     */
    @Override
    public abstract boolean equals(Object obj);

    /**
     * Computes the hash code of the {@link JsonElement}.
     * <br><br>
     * The hash code of the {@link JsonElement} is identical to the hash code of its underlying value,
     * regardless of the attached {@link SourceSpan}. {@link com.manchickas.john.ast.primitive.JsonNull JSON nulls} always
     * yield a hash code value of {@code 0}.
     *
     * @return the hash code of the {@link JsonElement}.
     */
    @Override
    public abstract int hashCode();

    /**
     * Stringifies the {@link JsonElement} into a <b>minified</b> JSON string.
     *
     * @return the stringified representation of the {@link JsonElement}.
     */
    @Override
    public String toString() {
        return John.stringify(this, 0);
    }

    @Nullable
    @ApiStatus.Internal
    public SourceSpan span() {
        return this.span;
    }
}
