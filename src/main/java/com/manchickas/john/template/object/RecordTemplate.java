package com.manchickas.john.template.object;

import com.google.common.collect.ImmutableMap;
import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.ast.JsonObject;
import com.manchickas.john.template.object.property.PropertyTemplate;
import com.manchickas.john.template.Result;
import com.manchickas.john.template.Template;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.List;

public abstract class RecordTemplate<Instance> implements Template<Instance> {

    @Override
    public Result<JsonElement> serialize(Instance value) {
        var props = this.properties();
        var builder = ImmutableMap.<String, JsonElement>builderWithExpectedSize(props.size());
        for (var property : props) {
            var result = property.serializeProperty(value);
            if (result.isError())
                return result;
            builder.put(property.property(), result.unwrap());
        }
        return Result.success(new JsonObject(builder.build()));
    }

    @Override
    public String name(IntSet encountered) {
        if (encountered.add(this.hashCode())) {
            var builder = new StringBuilder("{ ");
            var props = this.properties();
            for (var i = 0; i < props.size(); i++) {
                var property = props.get(i);
                if (i > 0)
                    builder.append(", ");
                builder.append(property.name(encountered));
            }
            return builder.append(" }")
                    .toString();
        }
        return ">...";
    }

    protected abstract List<PropertyTemplate<Instance, ?>> properties();
}
