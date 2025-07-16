package com.manchickas.john.ast;

import com.google.common.collect.ImmutableMap;
import com.manchickas.john.John;
import com.manchickas.john.exception.JsonException;
import com.manchickas.john.position.SourceSpan;
import org.jetbrains.annotations.NotNull;

public final class JsonObject extends JsonElement {

    private final ImmutableMap<String, JsonElement> elements;

    public JsonObject(ImmutableMap<String, JsonElement> elements) {
        this(null, elements);
    }

    public JsonObject(SourceSpan span, ImmutableMap<String, JsonElement> elements) {
        super(span);
        this.elements = elements;
    }

    @Override
    public String stringifyPattern() {
        var builder = new StringBuilder("{\\+n");
        var i = 0;
        for (var entry : this.elements.entrySet()) {
            if (i++ > 0)
                builder.append(",\\s\\n");
            var key = entry.getKey();
            var value = entry.getValue();
            builder.append('"')
                    .append(key)
                    .append('"')
                    .append(":\\s")
                    .append(value.stringifyPattern());
        }
        return builder.append("\\-n}")
                .toString();
    }

    @Override
    public @NotNull JsonElement property(String name) throws JsonException {
        var el = this.elements.get(name);
        if (el != null)
            return el;
        throw new JsonException("Expected the object to contain the property '%s'", name)
                .withSpan(this.span);
    }

    public JsonObject with(String name, JsonElement value) {
        return new JsonObject(this.span, ImmutableMap.<String, JsonElement>builder()
                .put(name, value)
                .putAll(this.elements)
                .buildKeepingLast());
    }

    @Override
    public String toString() {
        return John.stringify(this);
    }
}
