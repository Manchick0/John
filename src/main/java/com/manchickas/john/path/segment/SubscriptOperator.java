package com.manchickas.john.path.segment;

import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.exception.JsonException;
import org.jetbrains.annotations.NotNull;

public record SubscriptOperator(PathSegment operand, int index) implements PathSegment {

    @Override
    public @NotNull JsonElement resolve(JsonElement root) throws JsonException {
        return this.operand.resolve(root)
                .subscript(this.index);
    }

    @Override
    public @NotNull String toString() {
        return this.operand.toString() + '[' + this.index + ']';
    }

    @Override
    public int depth() {
        return this.operand.depth() + 1;
    }
}
