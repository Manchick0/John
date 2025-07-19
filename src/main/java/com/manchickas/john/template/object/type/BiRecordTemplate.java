package com.manchickas.john.template.object.type;

import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.ast.JsonObject;
import com.manchickas.john.template.object.property.PropertyTemplate;
import com.manchickas.john.template.Result;
import com.manchickas.john.template.object.RecordTemplate;
import com.manchickas.john.template.object.constructor.BiConstructor;

import java.util.List;

public final class BiRecordTemplate<A, B, Instance> extends RecordTemplate<Instance> {

    private final PropertyTemplate<Instance, A> first;
    private final PropertyTemplate<Instance, B> second;
    private final BiConstructor<A, B, Instance> constructor;

    public BiRecordTemplate(PropertyTemplate<Instance, A> first,
                            PropertyTemplate<Instance, B> second,
                            BiConstructor<A, B, Instance> constructor) {
        this.first = first;
        this.second = second;
        this.constructor = constructor;
    }

    @Override
    public Result<Instance> parse(JsonElement element) {
        if (element instanceof JsonObject)
            return this.first.wrapParseMismatch(element).flatMap(first ->
                this.second.wrapParseMismatch(element).flatMap(second -> {
                    var instance = this.constructor.construct(first, second);
                    return Result.success(instance);
                }));
        return Result.mismatch();
    }

    @Override
    protected List<PropertyTemplate<Instance, ?>> properties() {
        return List.of(this.first, this.second);
    }
}
