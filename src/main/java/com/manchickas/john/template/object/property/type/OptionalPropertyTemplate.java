package com.manchickas.john.template.object.property.type;

import com.manchickas.john.position.SourceSpan;
import com.manchickas.john.template.Result;
import com.manchickas.john.template.Template;
import com.manchickas.john.template.object.property.PropertyAccessor;
import com.manchickas.john.template.object.property.PropertyTemplate;

public class OptionalPropertyTemplate<Instance, T> extends PropertyTemplate<Instance, T> {

    public OptionalPropertyTemplate(String property,
                                    Template<T> template,
                                    PropertyAccessor<Instance, T> accessor) {
        super(property, template, accessor);
    }

    @Override
    public PropertyTemplate<Instance, T> orElse(T other) {
        return new DefaultedPropertyTemplate<>(
                this.property,
                this.template,
                this.accessor,
                other
        );
    }

    @Override
    public PropertyTemplate<Instance, T> optional() {
        return this;
    }

    @Override
    protected Result<T> missingResult(SourceSpan span) {
        return Result.success(null);
    }

    @Override
    protected boolean omitNulls() {
        return true;
    }
}
