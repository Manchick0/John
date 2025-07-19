package com.manchickas.john.template.object.type;

import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.ast.JsonObject;
import com.manchickas.john.template.object.property.PropertyTemplate;
import com.manchickas.john.template.Result;
import com.manchickas.john.template.object.RecordTemplate;
import com.manchickas.john.template.object.constructor.TriConstructor;

import java.util.List;

public final class TriRecordTemplate<A, B, C, Instance> extends RecordTemplate<Instance> {

    private final PropertyTemplate<Instance, A> first;
    private final PropertyTemplate<Instance, B> second;
    private final PropertyTemplate<Instance, C> third;
    private final TriConstructor<A, B, C, Instance> constructor;

    public TriRecordTemplate(PropertyTemplate<Instance, A> first,
                             PropertyTemplate<Instance, B> second,
                             PropertyTemplate<Instance, C> third,
                             TriConstructor<A, B, C, Instance> constructor) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.constructor = constructor;
    }

    @Override
    public Result<Instance> parse(JsonElement element) {
        if (element instanceof JsonObject)
            return this.first.parseAndPromote(element).flatMap(first ->
                this.second.parseAndPromote(element).flatMap(second ->
                        this.third.parseAndPromote(element).flatMap(third -> {
                            var instance = this.constructor.construct(first, second, third);
                            return Result.success(instance);
                        })));
        return Result.mismatch();
    }

    @Override
    protected List<PropertyTemplate<Instance, ?>> properties() {
        return List.of(this.first, this.second, this.third);
    }
}
