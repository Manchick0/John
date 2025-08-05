package com.manchickas.john.ast;

import com.google.common.collect.ImmutableMap;
import com.manchickas.john.exception.JsonException;
import com.manchickas.john.position.SourceSpan;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

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
        if (this.elements.isEmpty())
            return "{}";
        var builder = new StringBuilder("{\\+n");
        var i = 0;
        for (var entry : this.elements.entrySet()) {
            if (i++ > 0)
                builder.append(",\\n");
            var key = entry.getKey();
            var value = entry.getValue();
            builder.append('"')
                    .append(key)
                    .append('"')
                    .append(":\\s?")
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
        throw new JsonException("Expected the object to include '%s' as a property.", name)
                .withSpan(this.span);
    }

    public JsonObject with(String name, JsonElement value) {
        var builder = ImmutableMap.<String, JsonElement>builder();
        builder.put(name, value);
        for (var entry : this.entries()) {
            var key = entry.getKey();
            if (!key.equals(name))
                builder.put(entry);
        }
        return new JsonObject(this.span, builder.build());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof JsonObject other) {
            var span = other.span();
            if (span == null || this.span == null || this.span.equals(span))
                return this.elements.equals(other.elements);
            return false;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.elements.hashCode();
    }

    public Set<Map.Entry<String, JsonElement>> entries() {
        return this.elements.entrySet();
    }

    @Override
    public int length() {
        return this.elements.size();
    }
}
