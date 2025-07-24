package com.manchickas.john.template.array;

import com.manchickas.john.ast.JsonArray;
import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.exception.JsonException;
import com.manchickas.john.template.Result;
import com.manchickas.john.template.Template;
import com.manchickas.john.util.ArrayBuilder;
import it.unimi.dsi.fastutil.ints.IntSet;

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
                    var result = this.template.parseAndPromote(el);
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
        if (value != null) {
            var builder = ArrayBuilder.<JsonElement>builderWithExpectedSize(value.length);
            for (var v : value) {
                var result = this.template.serialize(v);
                if (result.isSuccess()) {
                    builder.append(result.unwrap());
                    continue;
                }
                return result;
            }
            return Result.success(new JsonArray(builder.build(JsonElement[]::new)));
        }
        return Result.mismatch();
    }

    @Override
    public String name(IntSet encountered) {
        return this.template.name(encountered) + "[]";
    }
}
