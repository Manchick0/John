package com.manchickas.john.template.number;

import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.position.SourceSpan;
import com.manchickas.john.template.Result;
import com.manchickas.john.template.Template;
import it.unimi.dsi.fastutil.ints.IntSet;

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
                if (value != null) {
                    if (value.doubleValue() % 1 == 0)
                        return NumericTemplate.this.serialize(value);
                    return Result.error("Expected the number to not include a fractional part.",
                            SourceSpan.lineWide(value.toString(), 1));
                }
                return Result.mismatch();
            }

            @Override
            public String name(IntSet encountered) {
                return NumericTemplate.this.name(encountered);
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
