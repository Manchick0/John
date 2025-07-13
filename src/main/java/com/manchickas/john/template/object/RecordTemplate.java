package com.manchickas.john.template.object;

import com.google.common.collect.ImmutableMap;
import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.ast.JsonObject;
import com.manchickas.john.util.Result;
import com.manchickas.john.template.Template;
import com.manchickas.john.template.object.property.PropertyTemplate;

import java.util.List;

public abstract class RecordTemplate<T> implements Template<T> {

    @Override
    public Result<JsonElement> serialize(T value) {
        var props = this.properties();
        var builder = ImmutableMap.<String, JsonElement>builderWithExpectedSize(props.size());
        for (var property : props) {
            var result = property.serializeProperty(value);
            if (result.isError())
                return result;
            builder.put(property.name, result.unwrap());
        }
        return Result.success(new JsonObject(builder.build()));
    }

    @Override
    public String name() {
        var builder = new StringBuilder("{ ");
        var props = this.properties();
        for (var i = 0; i < props.size(); i++) {
            var property = props.get(i);
            if (i > 0)
                builder.append(", ");
            builder.append(property.name());
        }
        return builder.append(" }")
                .toString();
    }

    protected abstract List<PropertyTemplate<T, ?>> properties();
}
