package com.manchickas.john.ast.primitive;

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
    public String stringifyPattern() {
        return this.value.toString();
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
