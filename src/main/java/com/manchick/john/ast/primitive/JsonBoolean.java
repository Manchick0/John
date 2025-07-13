package com.manchick.john.ast.primitive;

import com.manchick.john.util.JsonBuilder;
import com.manchick.john.position.SourceSpan;
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
