package com.manchickas.john.template.number;

import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.position.SourceSpan;
import com.manchickas.john.template.number.type.RangeTemplate;
import com.manchickas.john.util.Result;
import com.manchickas.john.template.Template;

public interface NumericTemplate extends Template<Number> {

    default NumericTemplate requireWhole() {
        return new NumericTemplate() {

            @Override
            public Result<Number> parse(JsonElement element) {
                var result = NumericTemplate.this.parse(element);
                if (result.isSuccess()) {
                    var value = result.unwrap();
                    if (value.doubleValue() % 1 == 0)
                        return result;
                    return Result.error("Expected the number to not include a fractional part.",
                            element.span());
                }
                return result;
            }

            @Override
            public Result<JsonElement> serialize(Number value) {
                if (value.doubleValue() % 1 == 0)
                    return NumericTemplate.this.serialize(value);
                return Result.error("Expected the number to not include a fractional part.",
                        SourceSpan.lineWide(value.toString(), 1));
            }

            @Override
            public String name(boolean potentialRecursion) {
                var name = NumericTemplate.this.name();
                if (NumericTemplate.this instanceof RangeTemplate)
                    return "~(" + name + ")";
                return "~" + name;
            }
        };
    }

    default Template<Byte> asByte() {
        return this.map(Number::byteValue, b -> b);
    }

    default Template<Short> asShort() {
        return this.map(Number::shortValue, s -> s);
    }

    default Template<Integer> asInteger() {
        return this.map(Number::intValue, i -> i);
    }

    default Template<Long> asLong() {
        return this.map(Number::longValue, l -> l);
    }

    default Template<Float> asFloat() {
        return this.map(Number::floatValue, f -> f);
    }

    default Template<Double> asDouble() {
        return this.map(Number::doubleValue, d -> d);
    }
}
