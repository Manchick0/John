package com.manchickas.john.template.string;

import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.ast.primitive.JsonString;
import com.manchickas.john.template.Result;
import com.manchickas.john.template.Template;
import it.unimi.dsi.fastutil.ints.IntSet;

public final class LiteralTemplate implements Template<String> {

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

    /**
     * Returns a <b>case-insensitive</b> version of the current {@link LiteralTemplate}.
     * @return a case-insensitive {@link LiteralTemplate}
     */
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

    @Override
    public Result<JsonElement> serialize(String value) {
        if (value != null) {
            if (value.equals(this.literal))
                return Result.success(new JsonString(this.literal));
            return Result.mismatch();
        }
        return Result.mismatch();
    }

    @Override
    public String name(IntSet encountered) {
        return '"' + this.literal + '"';
    }

    private boolean matches(String value) {
        if (this.caseSensitive)
            return value.equals(this.literal);
        return value.equalsIgnoreCase(this.literal);
    }
}
