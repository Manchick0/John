package com.manchickas.john.template;

import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.ast.primitive.JsonNull;
import com.manchickas.john.util.Result;

public final class NullTemplate implements Template<Void> {

    NullTemplate() {}

    @Override
    public Result<Void> parse(JsonElement element) {
        if (element instanceof JsonNull)
            return Result.success(null);
        return Result.mismatch();
    }

    @Override
    public Result<JsonElement> serialize(Void value) {
        return Result.success(new JsonNull());
    }

    /**
     * Composes a {@link Template} that transforms the {@link Void} produced by the {@link NullTemplate}
     * into a correctly-typed {@code null}, based on the most appropriate type.
     *
     * @param <V> the type of the resulting template.
     * @return a {@link Template} that yields a correctly-typed {@code null} on {@code null} values.
     * @since 1.0.0
     */
    public <V> Template<V> mapToTyped() {
        return this.map(__ -> null, __ -> null);
    }

    @Override
    public String name() {
        return "null";
    }
}
