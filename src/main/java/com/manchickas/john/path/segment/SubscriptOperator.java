package com.manchickas.john.path.segment;

import com.manchickas.john.exception.JsonException;
import com.manchickas.john.ast.JsonElement;
import org.jetbrains.annotations.NotNull;

public record SubscriptOperator(PathSegment operand, int index) implements PathSegment {

    @Override
    public @NotNull JsonElement resolve(JsonElement root) throws JsonException {
        return this.operand.resolve(root)
                .subscript(this.index);
    }

    @Override
    public int depth() {
        return this.operand.depth() + 1;
    }

    @Override
    public @NotNull String toString() {
        return this.operand.toString() + '[' + this.index + ']';
    }
}
