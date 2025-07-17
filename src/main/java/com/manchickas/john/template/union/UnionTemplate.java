package com.manchickas.john.template.union;

import com.google.common.collect.ImmutableSet;
import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.util.Result;
import com.manchickas.john.template.Template;

import java.util.Set;

public final class UnionTemplate<T> implements Template<T> {

    private final Set<Template<T>> templates;

    public UnionTemplate(Template<T>[] templates) {
        this.templates = ImmutableSet.copyOf(templates);
    }

    @Override
    public Result<T> parse(JsonElement element) {
        for (var template : this.templates) {
            var result = template.parse(element);
            if (result.isSuccess())
                return result;
        }
        return Result.mismatch();
    }

    @Override
    public Result<JsonElement> serialize(T value) {
        for (var template : this.templates) {
            var result = template.serialize(value);
            if (result.isSuccess())
                return result;
        }
        return Result.mismatch();
    }

    @Override
    public String name(boolean potentialRecursion) {
        var builder = new StringBuilder();
        var i = 0;
        for (var template : this.templates) {
            if (i++ > 0)
                builder.append(" | ");
            builder.append(template.name(potentialRecursion));
        }
        return builder.toString();
    }
}
