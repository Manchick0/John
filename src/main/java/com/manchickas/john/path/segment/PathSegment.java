package com.manchickas.john.path.segment;

import com.manchickas.john.exception.JsonException;
import com.manchickas.john.ast.JsonElement;

@FunctionalInterface
public interface PathSegment {

    PathSegment THIS = new PathSegment() {

        @Override
        public JsonElement resolve(JsonElement root) {
            return root;
        }

        @Override
        public String toString() {
            return ".";
        }
    };

    PathSegment PARENT = new PathSegment() {

        @Override
        public JsonElement resolve(JsonElement root) {
            return root.parent();
        }

        @Override
        public String toString() {
            return "..";
        }
    };

    JsonElement resolve(JsonElement root) throws JsonException;
}
