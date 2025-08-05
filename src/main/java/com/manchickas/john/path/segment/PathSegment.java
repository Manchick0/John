package com.manchickas.john.path.segment;

import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.exception.JsonException;
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

    @Override
    String toString();

    int depth();
}
