package com.manchickas.john.template.object;

import com.google.common.collect.ImmutableMap;
import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.ast.JsonObject;
import com.manchickas.john.template.Result;
import com.manchickas.john.template.Template;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.Map;

public final class MapTemplate<T> implements Template<Map<String, T>> {

    private final Template<T> template;

    public MapTemplate(Template<T> template) {
        this.template = template;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Result<Map<String, T>> parse(JsonElement element) {
        if (element instanceof JsonObject object) {
            var builder = ImmutableMap.<String, T>builderWithExpectedSize(object.length());
            for (var entry : object.entries()) {
                var key = entry.getKey();
                var value = entry.getValue();
                var result = this.template.parseAndPromote(value);
                if (result.isSuccess()) {
                    builder.put(key, result.unwrap());
                    continue;
                }
                return (Result<Map<String, T>>) result;
            }
            return Result.success(builder.build());
        }
        return Result.mismatch();
    }

    @Override
    public Result<JsonElement> serialize(Map<String, T> map) {
        var builder = ImmutableMap.<String, JsonElement>builderWithExpectedSize(map.size());
        for (var entry : map.entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();
            var result = this.template.serializeAndPromote(value);
            if (result.isError())
                return result;
            builder.put(key, result.unwrap());
        }
        return Result.success(new JsonObject(builder.build()));
    }

    @Override
    public String name(IntSet encountered) {
        return "{ [key: string]: " + this.template.name(encountered) + " }";
    }
}
