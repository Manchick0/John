package com.manchickas.john.template.string.type;

import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.ast.primitive.JsonString;
import com.manchickas.john.template.string.StringTemplate;
import com.manchickas.john.util.Result;
import com.manchickas.john.template.Template;

import java.util.regex.Pattern;

public final class PatternTemplate implements StringTemplate {

    private final Pattern pattern;

    public PatternTemplate(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public Template<String> caseInsensitive() {
        var flags = this.pattern.flags();
        if ((flags & Pattern.CASE_INSENSITIVE) == Pattern.CASE_INSENSITIVE)
            return this;
        var insensitive = Pattern.compile(this.pattern.pattern(), flags | Pattern.CASE_INSENSITIVE);
        return new PatternTemplate(insensitive);
    }

    @Override
    public Result<String> parse(JsonElement element) {
        if (element instanceof JsonString string) {
            var value = string.value();
            var matcher = this.pattern.matcher(value);
            if (matcher.matches())
                return Result.success(value);
            return Result.mismatch();
        }
        return Result.mismatch();
    }

    @Override
    public Result<JsonElement> serialize(String value) {
        var matcher = this.pattern.matcher(value);
        if (matcher.matches())
            return Result.success(new JsonString(value));
        return Result.mismatch();
    }

    @Override
    public String name(boolean potentialRecursion) {
        return this.pattern.toString();
    }
}
