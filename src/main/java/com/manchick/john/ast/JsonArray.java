package com.manchick.john.ast;

import com.manchick.john.exception.JsonException;
import com.manchick.john.util.JsonBuilder;
import com.manchick.john.position.SourceSpan;
import org.jetbrains.annotations.NotNull;

public final class JsonArray extends JsonElement {

    private final JsonElement[] elements;

    public JsonArray(JsonElement[] elements) {
        this(null, elements);
    }

    public JsonArray(SourceSpan span, JsonElement[] elements) {
        super(span);
        this.elements = elements;
        for (var element : this.elements)
            element.assignParent(this);
    }

    @Override
    public void stringify(JsonBuilder builder) {
        builder.append('[')
                .nest()
                .appendLine();
        for (var i = 0; i < this.elements.length; i++) {
            var el = this.elements[i];
            if (i > 0)
                builder.append(',')
                        .appendLine();
            builder.appendElement(el);
        }
        builder.flatten()
                .appendLine()
                .append(']');
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
