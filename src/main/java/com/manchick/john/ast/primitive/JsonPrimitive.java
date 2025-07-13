package com.manchick.john.ast.primitive;

import com.manchick.john.ast.JsonElement;
import com.manchick.john.position.SourceSpan;
import org.jetbrains.annotations.Nullable;

public abstract class JsonPrimitive<T> extends JsonElement {

    public JsonPrimitive() {
        this(null);
    }

    public JsonPrimitive(@Nullable SourceSpan span) {
        super(span);
    }

    public abstract T value();
}
