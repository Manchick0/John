package com.manchickas.john.path.segment;

import com.manchickas.john.exception.JsonException;
import com.manchickas.john.ast.JsonElement;
import org.jetbrains.annotations.NotNull;

public record PropertySegment(String name) implements PathSegment {

    public PropertySegment(String name) {
        this.name = name;
    }

    @Override
    public @NotNull JsonElement resolve(JsonElement root) throws JsonException {
        return root.property(this.name);
    }

    @Override
    public int depth() {
        return 1;
    }

    @Override
    public @NotNull String toString() {
        return this.name;
    }
}
