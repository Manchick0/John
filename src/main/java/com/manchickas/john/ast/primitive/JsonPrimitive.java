package com.manchickas.john.ast.primitive;

import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.position.SourceSpan;
import org.jetbrains.annotations.Nullable;

public abstract class JsonPrimitive<T> extends JsonElement {

    public JsonPrimitive(@Nullable SourceSpan span) {
        super(span);
    }

    public abstract T value();
}
