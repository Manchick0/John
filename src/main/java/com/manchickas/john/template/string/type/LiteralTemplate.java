package com.manchickas.john.template.string.type;

import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.ast.primitive.JsonString;
import com.manchickas.john.template.string.StringTemplate;
import com.manchickas.john.util.Result;
import com.manchickas.john.template.Template;

public final class LiteralTemplate implements StringTemplate {

    private final String literal;
    private final boolean caseSensitive;

    public LiteralTemplate(String literal) {
        this(literal, true);
    }

    private LiteralTemplate(String literal,
                           boolean caseSensitive) {
        this.literal = literal;
        this.caseSensitive = caseSensitive;
    }

    @Override
    public Template<String> caseInsensitive() {
        return new LiteralTemplate(this.literal, false);
    }

    @Override
    public Result<String> parse(JsonElement element) {
        if (element instanceof JsonString string) {
            var value = string.value();
            if (this.matches(value))
                return Result.success(value);
            return Result.mismatch();
        }
        return Result.mismatch();
    }

    private boolean matches(String value) {
        if (this.caseSensitive)
            return value.equals(this.literal);
        return value.equalsIgnoreCase(this.literal);
    }

    @Override
    public Result<JsonElement> serialize(String value) {
        if (value.equals(this.literal))
            return Result.success(new JsonString(this.literal));
        return Result.mismatch();
    }

    @Override
    public String name(boolean potentialRecursion) {
        return '"' + this.literal + '"';
    }
}
