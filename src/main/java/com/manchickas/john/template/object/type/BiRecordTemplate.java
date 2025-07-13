package com.manchickas.john.template.object.type;

import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.ast.JsonObject;
import com.manchickas.john.util.Result;
import com.manchickas.john.template.object.RecordTemplate;
import com.manchickas.john.template.object.constructor.BiConstructor;
import com.manchickas.john.template.object.property.PropertyTemplate;

import java.util.List;

public final class BiRecordTemplate<A, B, T> extends RecordTemplate<T> {

    private final PropertyTemplate<T, A> first;
    private final PropertyTemplate<T, B> second;
    private final BiConstructor<A, B, T> constructor;

    public BiRecordTemplate(PropertyTemplate<T, A> first,
                             PropertyTemplate<T, B> second,
                             BiConstructor<A, B, T> constructor) {
        this.first = first;
        this.second = second;
        this.constructor = constructor;
    }

    @Override
    public Result<T> parse(JsonElement element) {
        if (element instanceof JsonObject)
            return this.first.wrapParseMismatch(element).flatMap(first ->
                this.second.wrapParseMismatch(element).flatMap(second -> {
                    var instance = this.constructor.construct(first, second);
                    return Result.success(instance);
                }));
        return Result.mismatch();
    }

    @Override
    protected List<PropertyTemplate<T, ?>> properties() {
        return List.of(this.first, this.second);
    }
}
