package com.manchickas.john.ast.primitive;

import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.position.SourceSpan;
import org.jetbrains.annotations.Nullable;

public abstract class JsonPrimitive<T> extends JsonElement {

    public JsonPrimitive(@Nullable SourceSpan span) {
        super(span);
    }

    @Override
    public int length() {
        return 1;
    }

    public abstract T value();
}
