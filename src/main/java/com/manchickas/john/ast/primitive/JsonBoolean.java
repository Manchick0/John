package com.manchickas.john.ast.primitive;

import com.manchickas.john.util.JsonBuilder;
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
    public void stringify(JsonBuilder builder) {
        builder.append(this.value);
    }

    @Override
    public Boolean value() {
        return this.value;
    }
}
