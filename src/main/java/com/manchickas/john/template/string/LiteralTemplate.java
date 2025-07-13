package com.manchickas.john.template.string;

import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.ast.primitive.JsonString;
import com.manchickas.john.util.Result;
import com.manchickas.john.template.Template;

public final class LiteralTemplate implements Template<String> {

    private final String literal;

    public LiteralTemplate(String literal) {
        this.literal = literal;
    }

    @Override
    public Result<String> parse(JsonElement element) {
        if (element instanceof JsonString string
            && string.value().equals(this.literal)) {
            return Result.success(this.literal);
        }
        return Result.mismatch();
    }

    @Override
    public Result<JsonElement> serialize(String value) {
        if (value.equals(this.literal))
            return Result.success(new JsonString(this.literal));
        return Result.mismatch();
    }

    @Override
    public String name() {
        return '"' + this.literal + '"';
    }
}
