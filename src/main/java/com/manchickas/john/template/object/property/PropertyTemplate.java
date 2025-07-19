package com.manchickas.john.template.object.property;

import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.ast.JsonObject;
import com.manchickas.john.ast.primitive.JsonNull;
import com.manchickas.john.exception.JsonException;
import com.manchickas.john.position.SourceSpan;
import com.manchickas.john.template.Template;
import com.manchickas.john.util.Result;

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
    protected abstract Result<T> missingResult(SourceSpan span);

    public Result<JsonElement> serializeProperty(Instance instance) {
        var value = this.access(instance);
        if (value != null)
            return this.serialize(value);
        return Result.success(new JsonNull());
    };

    @Override
    public Result<T> parse(JsonElement element) {
        if (element instanceof JsonObject object) {
            try {
                var prop = object.property(this.property);
                return this.template.wrapParseMismatch(prop);
            } catch (JsonException e) {
                return this.missingResult(element.span());
            }
        }
        return Result.mismatch();
    }

    @Override
    public Result<JsonElement> serialize(T value) {
        return this.template.wrapSerializeMismatch(value);
    }

    @Override
    public String name() {
        return this.property + ": " + this.template.name();
    }

    public T access(Instance instance) {
        return this.accessor.access(instance);
    }

    public String property() {
        return this.property;
    }
}
