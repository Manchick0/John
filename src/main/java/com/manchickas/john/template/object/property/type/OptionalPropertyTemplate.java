package com.manchickas.john.template.object.property.type;

import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.position.SourceSpan;
import com.manchickas.john.template.Template;
import com.manchickas.john.template.object.property.PropertyAccessor;
import com.manchickas.john.template.object.property.PropertyTemplate;
import com.manchickas.john.util.Result;

public final class OptionalPropertyTemplate<Instance, T> extends PropertyTemplate<Instance, T> {

    private final T defaultValue;

    public OptionalPropertyTemplate(String property,
                                    Template<T> template,
                                    PropertyAccessor<Instance, T> accessor,
                                    T defaultValue) {
        super(property, template, accessor);
        this.defaultValue = defaultValue;
    }

    @Override
    public Result<T> missingResult(SourceSpan span) {
        return Result.success(this.defaultValue);
    }

    @Override
    public String name() {
        return this.property + "?: " + this.template.name();
    }
}
