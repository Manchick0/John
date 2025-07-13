package com.manchickas.john.ast.primitive;

import com.manchickas.john.util.JsonBuilder;
import com.manchickas.john.position.SourceSpan;

public final class JsonNull extends JsonPrimitive<Void> {

    public JsonNull() {
        this(null);
    }

    public JsonNull(SourceSpan span) {
        super(span);
    }

    @Override
    public void stringify(JsonBuilder builder) {
        builder.appendNull();
    }

    @Override
    public Void value() {
        return null;
    }
}
