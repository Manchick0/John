package com.manchick.john.template.string;

import com.manchick.john.ast.JsonElement;
import com.manchick.john.ast.primitive.JsonString;
import com.manchick.john.util.Result;
import com.manchick.john.template.Template;

import java.util.regex.Pattern;

public final class PatternTemplate implements Template<String> {

    private final Pattern pattern;

    public PatternTemplate(Pattern pattern) {
        this.pattern = pattern;
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
    public String name() {
        return this.pattern.toString();
    }
}
