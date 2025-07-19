package com.manchickas.john.template.object.type;

import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.ast.JsonObject;
import com.manchickas.john.template.object.property.PropertyTemplate;
import com.manchickas.john.template.Result;
import com.manchickas.john.template.object.RecordTemplate;
import com.manchickas.john.template.object.constructor.TetraConstructor;

import java.util.List;

public final class TetraRecordTemplate<A, B, C, D, Instance> extends RecordTemplate<Instance> {

    private final PropertyTemplate<Instance, A> first;
    private final PropertyTemplate<Instance, B> second;
    private final PropertyTemplate<Instance, C> third;
    private final PropertyTemplate<Instance, D> fourth;
    private final TetraConstructor<A, B, C, D, Instance> constructor;

    public TetraRecordTemplate(PropertyTemplate<Instance, A> first,
                               PropertyTemplate<Instance, B> second,
                               PropertyTemplate<Instance, C> third,
                               PropertyTemplate<Instance, D> fourth,
                               TetraConstructor<A, B, C, D, Instance> constructor) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
        this.constructor = constructor;
    }

    @Override
    public Result<Instance> parse(JsonElement element) {
        if (element instanceof JsonObject)
            return this.first.wrapParseMismatch(element).flatMap(first ->
                this.second.wrapParseMismatch(element).flatMap(second ->
                        this.third.wrapParseMismatch(element).flatMap(third ->
                                this.fourth.wrapParseMismatch(element).flatMap(fourth -> {
                                    var instance = this.constructor.construct(first, second, third, fourth);
                                    return Result.success(instance);
                                }))));
        return Result.mismatch();
    }

    @Override
    protected List<PropertyTemplate<Instance, ?>> properties() {
        return List.of(this.first, this.second, this.third, this.fourth);
    }
}
