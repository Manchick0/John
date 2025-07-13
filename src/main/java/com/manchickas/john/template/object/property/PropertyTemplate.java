package com.manchickas.john.template.object.property;

import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.ast.JsonObject;
import com.manchickas.john.exception.JsonException;
import com.manchickas.john.util.Result;
import com.manchickas.john.template.Template;

public final class PropertyTemplate<T, V> implements Template<V> {

    public final String name;
    private final Template<V> template;
    private final PropertyAccessor<T, V> accessor;

    public PropertyTemplate(String name, Template<V> template, PropertyAccessor<T, V> accessor) {
        this.name = name;
        this.template = template;
        this.accessor = accessor;
    }

    @Override
    public Result<V> parse(JsonElement element) {
        if (element instanceof JsonObject object) {
            try {
                var value = object.property(this.name);
                return this.template.wrapParseMismatch(value);
            } catch (JsonException e) {
                return Result.error("Expected the object to include '%s' as a property"
                        .formatted(this.name), element.span());
            }
        }
        return Result.mismatch();
    }

    @Override
    public Result<JsonElement> serialize(V value) {
        return this.template.wrapSerializeMismatch(value);
    }

    public Result<JsonElement> serializeProperty(T instance) {
        return this.serialize(this.access(instance));
    }

    public V access(T instance) {
        return this.accessor.access(instance);
    }

    @Override
    public String name() {
        return this.name + ": " + this.template.name();
    }
}
