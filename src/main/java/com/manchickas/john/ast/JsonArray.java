package com.manchickas.john.ast;

import com.manchickas.john.exception.JsonException;
import com.manchickas.john.position.SourceSpan;
import org.jetbrains.annotations.NotNull;

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

    @Override
    @NotNull
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
    public int length() {
        return this.elements.length;
    }
}
