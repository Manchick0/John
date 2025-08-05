package com.manchickas.john.ast.primitive;

import com.manchickas.john.position.SourceSpan;

public final class JsonNull extends JsonPrimitive<Void> {

    public JsonNull() {
        this(null);
    }

    public JsonNull(SourceSpan span) {
        super(span);
    }

    @Override
    public String stringifyPattern() {
        return "null";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof JsonNull other) {
            var span = other.span();
            return span == null || this.span == null || this.span.equals(span);
        }
        return false;
    }

    @Override
    public Void value() {
        return null;
    }
}
