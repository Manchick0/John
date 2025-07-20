package com.manchickas.john.ast.primitive;

import com.manchickas.john.position.SourceSpan;
import org.jetbrains.annotations.Nullable;

public final class JsonBoolean extends JsonPrimitive<Boolean> {

    private final boolean value;

    public JsonBoolean(boolean value) {
        this(null, value);
    }

    public JsonBoolean(@Nullable SourceSpan span, boolean value) {
        super(span);
        this.value = value;
    }

    @Override
    public String stringifyPattern() {
        return this.value ? "true" : "false";
    }

    @Override
    public Boolean value() {
        return this.value;
    }
}
