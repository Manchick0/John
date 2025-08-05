package com.manchickas.john.template.object.type;

import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.ast.JsonObject;
import com.manchickas.john.template.Result;
import com.manchickas.john.template.object.RecordTemplate;
import com.manchickas.john.template.object.constructor.UniConstructor;
import com.manchickas.john.template.object.property.PropertyTemplate;

import java.util.List;

public final class UniRecordTemplate<Instance, A> extends RecordTemplate<Instance> {

    private final PropertyTemplate<Instance, A, ?> first;
    private final UniConstructor<A, Instance> constructor;

    public UniRecordTemplate(PropertyTemplate<Instance, A, ?> first,
                             UniConstructor<A, Instance> constructor) {
        this.first = first;
        this.constructor = constructor;
    }

    @Override
    public Result<Instance> parse(JsonElement element) {
        if (element instanceof JsonObject)
            return this.first.parseAndPromote(element).flatMap(first -> {
                var instance = this.constructor.construct(first);
                return Result.success(instance);
            });
        return Result.mismatch();
    }

    @Override
    protected List<PropertyTemplate<Instance, ?, ?>> properties() {
        return List.of(this.first);
    }
}
