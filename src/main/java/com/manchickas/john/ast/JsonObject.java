package com.manchickas.john.ast;

import com.google.common.collect.ImmutableMap;
import com.manchickas.john.John;
import com.manchickas.john.exception.JsonException;
import com.manchickas.john.util.JsonBuilder;
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
        this.elements.forEach((key, value) ->
                value.assignParent(this));
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
    public void stringify(JsonBuilder builder) {
        builder.append('{')
                .nest()
                .appendLine();
        var i = 0;
        for (var entry : this.elements.entrySet()) {
            var key = entry.getKey();
            var element = entry.getValue();
            if (i > 0) {
                builder.append(',')
                        .appendLine();
            }
            builder.appendString(key)
                    .append(':')
                    .append(' ')
                    .appendElement(element);
            i++;
        }
        builder.flatten()
                .appendLine()
                .append('}');
    }

    @Override
    public int length() {
        return this.elements.size();
    }

    @Override
    public String toString() {
        return John.stringify(this);
    }
}
