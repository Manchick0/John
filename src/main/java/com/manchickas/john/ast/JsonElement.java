package com.manchickas.john.ast;

import com.manchickas.john.John;
import com.manchickas.john.exception.JsonException;
import com.manchickas.john.path.JsonPath;
import com.manchickas.john.position.SourceSpan;
import com.manchickas.john.template.Template;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class JsonElement {

    @Nullable
    protected final SourceSpan span;

    public JsonElement() {
        this(null);
    }

    public JsonElement(@Nullable SourceSpan span) {
        this.span = span;
    }

    @NotNull
    public <T> T expect(Template<T> template) throws JsonException {
        var result = template.wrapParseMismatch(this);
        if (result.isSuccess())
            return result.unwrap();
        throw new JsonException(result.message())
                .withSpan(result.span());
    }

    @NotNull
    public JsonElement get(String path) throws JsonException {
        return this.get(JsonPath.compile(path));
    }

    @NotNull
    public JsonElement get(JsonPath path) throws JsonException {
        return path.traverse(this);
    }

    @NotNull
    public <T> T get(String path, Template<T> template) throws JsonException {
        return this.get(JsonPath.compile(path), template);
    }

    @NotNull
    public <T> T get(JsonPath path, Template<T> template) throws JsonException {
        return this.get(path).expect(template);
    }

    /**
     * Attempts to retrieve the provided property from the {@link JsonObject}.
     *
     * @param name the property to retrieve.
     * @return the value of the provided property.
     * @throws JsonException in case the property is not present on the object, or the element doesn't represent an object in the first place.
     */
    @NotNull
    public JsonElement property(String name) throws JsonException {
        throw new JsonException("Expected an object.")
                .withSpan(this.span);
    }

    /**
     * Attempts to retrieve the provided index from the {@link JsonArray}.
     *
     * @param index the index to retrive.
     * @return the value at the provided index.
     * @throws JsonException in case a negative index is provided, the array contains too few elements, or the element doesn't represent an array in the first place.
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

    @Nullable
    public SourceSpan span() {
        return this.span;
    }
}
