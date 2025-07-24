package com.manchickas.john.template.object.property;

import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.ast.JsonObject;
import com.manchickas.john.exception.JsonException;
import com.manchickas.john.position.SourceSpan;
import com.manchickas.john.template.Template;
import com.manchickas.john.template.Result;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.Optional;

public abstract class PropertyTemplate<Instance, T> implements Template<T> {

    protected final String property;
    protected final Template<T> template;
    protected final PropertyAccessor<Instance, T> accessor;

    public PropertyTemplate(String property,
                            Template<T> template,
                            PropertyAccessor<Instance, T> accessor) {
        this.property = property;
        this.template = template;
        this.accessor = accessor;
    }

    /**
     * Composes a {@link Template} that supplies the provided {@code other} value if the necessary property isn't
     * present on the object.
     * <br><br>
     * The composed {@link Template} effectively never mismatches on {@link #parse(JsonElement)} operations.
     * @param other the default value to supply.
     * @return a {@link Template} that yields the current template's value, or {@code other} if the property wasn't present.
     * @since 1.0.0
     */
    @Override
    public abstract PropertyTemplate<Instance, T> orElse(T other);

    /**
     * Composes a {@link Template} that supplies {@code null} if the necessary property isn't present on the object.
     * <br><br>
     * If an <b>optional</b> property ends up being {@code null} during serialization, it's omitted altogether.
     *
     * @return a {@link Template} that yields the current template's value, or {@code null} if the property wasn't present.
     * @since 1.2.0
     */
    @Override
    public abstract PropertyTemplate<Instance, T> optional();

    protected abstract Result<T> missingResult(SourceSpan span);
    protected abstract boolean omitNulls();

    public Optional<Result<JsonElement>> serializeProperty(Instance instance) {
        var value = this.access(instance);
        if (value == null && this.omitNulls())
            return Optional.empty();
        return Optional.of(this.serialize(value));
    };

    @Override
    public Result<T> parse(JsonElement element) {
        if (element instanceof JsonObject object) {
            try {
                var prop = object.property(this.property);
                return this.template.parseAndPromote(prop);
            } catch (JsonException e) {
                return this.missingResult(element.span());
            }
        }
        return Result.mismatch();
    }

    @Override
    public Result<JsonElement> serialize(T value) {
        return this.template.serializeAndPromote(value);
    }

    @Override
    public String name(IntSet encountered) {
        return this.property + ": " + this.template.name(encountered);
    }

    public T access(Instance instance) {
        return this.accessor.access(instance);
    }

    public String property() {
        return this.property;
    }
}
