package com.manchickas.john.path.segment;

import com.manchickas.john.exception.JsonException;
import com.manchickas.john.ast.JsonElement;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface PathSegment {

    PathSegment THIS = new PathSegment() {

        @Override
        public @NotNull JsonElement resolve(JsonElement root) {
            return root;
        }

        @Override
        public String toString() {
            return ".";
        }
    };

    @NotNull
    JsonElement resolve(JsonElement root) throws JsonException;
}
