package com.manchick.john.template.array;

import com.manchick.john.ast.JsonArray;
import com.manchick.john.ast.JsonElement;
import com.manchick.john.exception.JsonException;
import com.manchick.john.util.Result;
import com.manchick.john.template.Template;
import com.manchick.john.util.ArrayBuilder;

import java.util.function.IntFunction;

public final class ArrayTemplate<T> implements Template<T[]> {

    private final Template<T> template;
    private final IntFunction<T[]> factory;

    public ArrayTemplate(Template<T> template, IntFunction<T[]> factory) {
        this.template = template;
        this.factory = factory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Result<T[]> parse(JsonElement element) {
        if (element instanceof JsonArray array) {
            var length = array.length();
            var builder = ArrayBuilder.<T>builderWithExpectedSize(length);
            for (var i = 0; i < length; i++) {
                try {
                    var el = array.subscript(i);
                    var result = this.template.wrapParseMismatch(el);
                    if (result.isSuccess()) {
                        builder.append(result.unwrap());
                        continue;
                    }
                    return (Result<T[]>) result;
                } catch (JsonException e) {
                    throw new AssertionError("Unreachable");
                }
            }
            return Result.success(builder.build(this.factory));
        }
        return Result.mismatch();
    }

    @Override
    public Result<JsonElement> serialize(T[] value) {
        var builder = ArrayBuilder.<JsonElement>builderWithExpectedSize(value.length);
        for (var v : value) {
            var result = this.template.wrapSerializeMismatch(v);
            if (result.isSuccess()) {
                builder.append(result.unwrap());
                continue;
            }
            return result;
        }
        return Result.success(new JsonArray(builder.build(JsonElement[]::new)));
    }

    @Override
    public String name() {
        return this.template.name() + "[]";
    }
}
