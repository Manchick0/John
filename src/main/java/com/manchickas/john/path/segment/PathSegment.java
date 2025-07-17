package com.manchickas.john.path.segment;

import com.manchickas.john.exception.JsonException;
import com.manchickas.john.ast.JsonElement;
import org.jetbrains.annotations.NotNull;

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

        @Override
        public int depth() {
            return 0;
        }
    };

    @NotNull
    JsonElement resolve(JsonElement root) throws JsonException;

    int depth();

    @Override
    String toString();
}
