package com.manchick.john.template.number;

import com.manchick.john.ast.JsonElement;
import com.manchick.john.ast.primitive.JsonNumber;
import com.manchick.john.util.Result;

public final class MaxTemplate implements NumericTemplate {

    private final double max;

    public MaxTemplate(Number max) {
        this.max = max.doubleValue();
    }

    @Override
    public Result<Number> parse(JsonElement element) {
        if (element instanceof JsonNumber number) {
            var value = number.value().doubleValue();
            if (value <= this.max)
                return Result.success(value);
            return Result.mismatch();
        }
        return Result.mismatch();
    }

    @Override
    public Result<JsonElement> serialize(Number value) {
        if (value.doubleValue() <= this.max)
            return Result.success(new JsonNumber(value));
        return Result.mismatch();
    }

    @Override
    public String name() {
        return ".." + this.max;
    }
}
