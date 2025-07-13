package com.manchick.john.path.segment;

import com.manchick.john.exception.JsonException;
import com.manchick.john.ast.JsonElement;
import org.jetbrains.annotations.NotNull;

public record SubscriptOperator(PathSegment operand, int index) implements PathSegment {

    @Override
    public JsonElement resolve(JsonElement root) throws JsonException {
        return this.operand.resolve(root)
                .subscript(this.index);
    }

    @Override
    public @NotNull String toString() {
        return this.operand.toString() + '[' + this.index + ']';
    }
}
