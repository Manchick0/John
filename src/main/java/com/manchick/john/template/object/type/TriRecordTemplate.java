package com.manchick.john.template.object.type;

import com.manchick.john.ast.JsonElement;
import com.manchick.john.ast.JsonObject;
import com.manchick.john.util.Result;
import com.manchick.john.template.object.RecordTemplate;
import com.manchick.john.template.object.constructor.TriConstructor;
import com.manchick.john.template.object.property.PropertyTemplate;

import java.util.List;

public final class TriRecordTemplate<A, B, C, T> extends RecordTemplate<T> {

    private final PropertyTemplate<T, A> first;
    private final PropertyTemplate<T, B> second;
    private final PropertyTemplate<T, C> third;
    private final TriConstructor<A, B, C, T> constructor;

    public TriRecordTemplate(PropertyTemplate<T, A> first,
                             PropertyTemplate<T, B> second,
                             PropertyTemplate<T, C> third,
                             TriConstructor<A, B, C, T> constructor) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.constructor = constructor;
    }

    @Override
    public Result<T> parse(JsonElement element) {
        if (element instanceof JsonObject)
            return this.first.wrapParseMismatch(element).flatMap(first ->
                this.second.wrapParseMismatch(element).flatMap(second ->
                        this.third.wrapParseMismatch(element).flatMap(third -> {
                            var instance = this.constructor.construct(first, second, third);
                            return Result.success(instance);
                        })));
        return Result.mismatch();
    }

    @Override
    protected List<PropertyTemplate<T, ?>> properties() {
        return List.of(this.first, this.second, this.third);
    }
}
