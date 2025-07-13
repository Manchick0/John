package com.manchickas.john.template.object.type;

import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.ast.JsonObject;
import com.manchickas.john.util.Result;
import com.manchickas.john.template.object.RecordTemplate;
import com.manchickas.john.template.object.constructor.UniConstructor;
import com.manchickas.john.template.object.property.PropertyTemplate;

import java.util.List;

public final class UniRecordTemplate<T, A> extends RecordTemplate<T> {

    private final PropertyTemplate<T, A> first;
    private final UniConstructor<A, T> constructor;

    public UniRecordTemplate(PropertyTemplate<T, A> first,
                             UniConstructor<A, T> constructor) {
        this.first = first;
        this.constructor = constructor;
    }

    @Override
    public Result<T> parse(JsonElement element) {
        if (element instanceof JsonObject)
            return this.first.wrapParseMismatch(element).flatMap(first -> {
                var instance = this.constructor.construct(first);
                return Result.success(instance);
            });
        return Result.mismatch();
    }

    @Override
    protected List<PropertyTemplate<T, ?>> properties() {
        return List.of(this.first);
    }
}
