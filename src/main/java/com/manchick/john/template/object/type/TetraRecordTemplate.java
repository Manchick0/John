package com.manchick.john.template.object.type;

import com.manchick.john.ast.JsonElement;
import com.manchick.john.ast.JsonObject;
import com.manchick.john.util.Result;
import com.manchick.john.template.object.RecordTemplate;
import com.manchick.john.template.object.constructor.TetraConstructor;
import com.manchick.john.template.object.property.PropertyTemplate;

import java.util.List;

public final class TetraRecordTemplate<A, B, C, D, T> extends RecordTemplate<T> {

    private final PropertyTemplate<T, A> first;
    private final PropertyTemplate<T, B> second;
    private final PropertyTemplate<T, C> third;
    private final PropertyTemplate<T, D> fourth;
    private final TetraConstructor<A, B, C, D, T> constructor;

    public TetraRecordTemplate(PropertyTemplate<T, A> first,
                               PropertyTemplate<T, B> second,
                               PropertyTemplate<T, C> third,
                               PropertyTemplate<T, D> fourth,
                               TetraConstructor<A, B, C, D, T> constructor) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
        this.constructor = constructor;
    }

    @Override
    public Result<T> parse(JsonElement element) {
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
    protected List<PropertyTemplate<T, ?>> properties() {
        return List.of(this.first, this.second, this.third, this.fourth);
    }
}
