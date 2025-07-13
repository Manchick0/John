package com.manchickas.john.ast.primitive;

import com.manchickas.john.util.JsonBuilder;
import com.manchickas.john.position.SourceSpan;

public final class JsonNumber extends JsonPrimitive<Number> {

    private final Number value;

    public JsonNumber(Number value) {
        this(null, value);
    }

    public JsonNumber(SourceSpan span, Number value) {
        super(span);
        this.value = value;
    }

    @Override
    public void stringify(JsonBuilder builder) {
        builder.append(this.value);
    }

    @Override
    public Number value() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.value.toString();
    }
}
