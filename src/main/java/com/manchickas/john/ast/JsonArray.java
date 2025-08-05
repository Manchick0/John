package com.manchickas.john.ast;

import com.manchickas.john.exception.JsonException;
import com.manchickas.john.position.SourceSpan;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public final class JsonArray extends JsonElement {

    private final JsonElement[] elements;

    public JsonArray(JsonElement[] elements) {
        this(null, elements);
    }

    public JsonArray(SourceSpan span, JsonElement[] elements) {
        super(span);
        this.elements = elements;
    }

    @Override
    public String stringifyPattern() {
        if (this.elements.length > 0) {
            var builder = new StringBuilder("[\\+n");
            for (var i = 0; i < this.length(); i++) {
                var element = this.elements[i];
                if (i > 0)
                    builder.append(",\\s?\\n");
                builder.append(element.stringifyPattern());
            }
            return builder.append("\\-n]")
                    .toString();
        }
        return "[]";
    }

    @NotNull
    @Override
    public JsonElement subscript(int index) throws JsonException {
        if (index >= 0) {
            if (index < this.length())
                return this.elements[index];
            throw new JsonException("Expected the array to contain at least %d elements.", index + 1)
                    .withSpan(this.span);
        }
        throw new JsonException("Attempted to access an element at index '%d' of a JSON array.", index);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof JsonArray other) {
            var span = other.span();
            if (span == null || this.span == null || this.span.equals(span))
                return Arrays.equals(this.elements, other.elements);
            return false;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.elements);
    }

    @Override
    public int length() {
        return this.elements.length;
    }
}
