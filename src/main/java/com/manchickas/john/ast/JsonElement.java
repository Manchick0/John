package com.manchickas.john.ast;

import com.manchickas.john.John;
import com.manchickas.john.exception.JsonException;
import com.manchickas.john.path.JsonPath;
import com.manchickas.john.position.SourceSpan;
import com.manchickas.john.template.Template;
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

    protected JsonElement() {
        this(null);
    }

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
    @NotNull
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
     *
     * @param path the path to traverse.
     * @param template the {@link Template} the element at the provided path must satisfy.
     * @return the parsed from the {@link JsonElement} value.
     * @throws JsonException if the JSON structure doesn't match the one expected by the path, or the retrieved {@link JsonElement} doesn't satisfy the provided {@link Template}.
     */
    @NotNull
    public <T> T get(String path, Template<T> template) throws JsonException {
        return this.get(JsonPath.compile(path), template);
    }

    /**
     * Attempts to parse the element at the provided precompiled {@link JsonPath} according to the provided {@link JsonElement}.
     *
     * @param path the path to traverse.
     * @param template the {@link Template} the element at the provided path must satisfy.
     * @return the parsed from the {@link JsonElement} value.
     * @throws JsonException if the JSON structure doesn't match the one expected by the path, or the retrieved {@link JsonElement} doesn't satisfy the provided {@link Template}.
     */
    @NotNull
    public <T> T get(JsonPath path, Template<T> template) throws JsonException {
        return this.get(path).expect(template);
    }

    /**
     * Attempts to retrieve the provided property from the {@link JsonElement}.
     *
     * @param name the property to retrieve.
     * @return the value of the provided property.
     * @throws JsonException if the current {@link JsonElement} doesn't represent an object,
     *      or the property is missing on the object.
     */
    @NotNull
    public JsonElement property(String name) throws JsonException {
        throw new JsonException("Expected an object.")
                .withSpan(this.span);
    }

    /**
     * Attempts to retrieve the provided index from the {@link JsonElement}.
     *
     * @param index the index to retrieve.
     * @return the value at the provided index.
     * @throws JsonException if the current {@link JsonElement} doesn't represent an object,
     *      a negative index was provided, or the array contains too few elements.
     */
    @NotNull
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
     * The {@code length} is an abstract concept that depends on the specific implementation. {@link com.manchickas.john.ast.primitive.JsonPrimitive JsonPrimitives}s
     * always have a length of {@code 1}, {@link JsonArray}s return their actual length, and {@link JsonObject}s — their underlying size.
     *
     * @return the length of the element.
     */
    public abstract int length();

    @Nullable
    public SourceSpan span() {
        return this.span;
    }

    @Override
    public String toString() {
        return John.stringify(this);
    }
}
