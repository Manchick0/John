package com.manchickas.john.path.segment;

import com.manchickas.john.exception.JsonException;
import com.manchickas.john.ast.JsonElement;

public final class PropertySegment implements PathSegment {

    private final String name;

    public PropertySegment(String name) {
        this.name = name;
    }

    @Override
    public JsonElement resolve(JsonElement root) throws JsonException {
        return root.property(this.name);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
