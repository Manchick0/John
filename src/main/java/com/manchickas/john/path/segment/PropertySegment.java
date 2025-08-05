package com.manchickas.john.path.segment;

import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.exception.JsonException;
import org.jetbrains.annotations.NotNull;

public record PropertySegment(String name) implements PathSegment {

    @Override
    public @NotNull JsonElement resolve(JsonElement root) throws JsonException {
        return root.property(this.name);
    }

    @Override
    public @NotNull String toString() {
        return this.name;
    }

    @Override
    public int depth() {
        return 1;
    }
}
