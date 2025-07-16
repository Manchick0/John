package com.manchickas.john.path.segment;

import com.manchickas.john.exception.JsonException;
import com.manchickas.john.ast.JsonElement;
import org.jetbrains.annotations.NotNull;

public final class PropertySegment implements PathSegment {

    private final String name;

    public PropertySegment(String name) {
        this.name = name;
    }

    @Override
    public @NotNull JsonElement resolve(JsonElement root) throws JsonException {
        return root.property(this.name);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
