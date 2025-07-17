package com.manchickas.john.template.object.property.type;

import com.manchickas.john.position.SourceSpan;
import com.manchickas.john.template.LazyTemplate;
import com.manchickas.john.template.Template;
import com.manchickas.john.template.object.property.PropertyAccessor;
import com.manchickas.john.template.object.property.PropertyTemplate;
import com.manchickas.john.util.Result;

public final class DefaultedPropertyTemplate<Instance, T> extends PropertyTemplate<Instance, T> {

    private final T other;

    public DefaultedPropertyTemplate(String property,
                                     Template<T> template,
                                     PropertyAccessor<Instance, T> accessor,
                                     T other) {
        super(property, template, accessor);
        this.other = other;
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
    protected Result<T> missingResult(SourceSpan span) {
        return Result.success(this.other);
    }

    @Override
    public String name(boolean potentialRecursion) {
        return this.property + "?: " + this.template.name(true);
    }
}
