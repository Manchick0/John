package com.manchickas.john.template.number.type;

import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.ast.primitive.JsonNumber;
import com.manchickas.john.template.number.NumericTemplate;
import com.manchickas.john.template.Result;
import it.unimi.dsi.fastutil.ints.IntSet;

public final class RangeTemplate implements NumericTemplate {

    private final double min;
    private final double max;

    public RangeTemplate(Number min, Number max) {
        this.min = min.doubleValue();
        this.max = max.doubleValue();
    }

    @Override
    public Result<Number> parse(JsonElement element) {
        if (element instanceof JsonNumber number) {
            var value = number.value().doubleValue();
            if (value >= this.min && value <= this.max)
                return Result.success(value);
            return Result.mismatch();
        }
        return Result.mismatch();
    }

    @Override
    public Result<JsonElement> serialize(Number value) {
        if (value != null) {
            var val = value.doubleValue();
            if (val >= this.min && val <= this.max)
                return Result.success(new JsonNumber(value));
            return Result.mismatch();
        }
        return Result.mismatch();
    }

    @Override
    public String name(IntSet encountered) {
        return this.min + ".." + this.max;
    }
}
