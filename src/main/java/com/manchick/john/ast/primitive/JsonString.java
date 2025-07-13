package com.manchick.john.ast.primitive;

import com.manchick.john.util.JsonBuilder;
import com.manchick.john.position.SourceSpan;

public final class JsonString extends JsonPrimitive<String> {

    private final String value;

    public JsonString(String value) {
        this(null, value);
    }

    public JsonString(SourceSpan span, String value) {
        super(span);
        this.value = value;
    }

    @Override
    public void stringify(JsonBuilder builder) {
        builder.appendString(this.value);
    }

    @Override
    public String value() {
        return this.value;
    }

    @Override
    public String toString() {
        return '"' + this.value + '"';
    }
}
