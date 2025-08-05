package com.manchickas.john.template.object.property.type;

import com.manchickas.john.position.SourceSpan;
import com.manchickas.john.template.Result;
import com.manchickas.john.template.Template;
import com.manchickas.john.template.object.property.PropertyAccessor;
import com.manchickas.john.template.object.property.PropertyTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import java.util.function.Supplier;

public final class RequiredPropertyTemplate<Instance, T> extends PropertyTemplate<Instance, T, RequiredPropertyTemplate<Instance, T>> {

    public RequiredPropertyTemplate(String property,
                                    Template<T> template,
                                    PropertyAccessor<Instance, T> accessor,
                                    Predicate<T> omitRule) {
        super(property, template, accessor, omitRule);
    }

    @Override
    public OptionalPropertyTemplate<Instance, T> optional(@NotNull Supplier<@Nullable T> supplier) {
        return new OptionalPropertyTemplate<>(
                this.property,
                this.template.optional(supplier),
                this.accessor,
                this.omitRule,
                supplier
        );
    }

    @Override
    public RequiredPropertyTemplate<Instance, T> omitWhen(Predicate<T> omitRule) {
        return new RequiredPropertyTemplate<>(
                this.property,
                this.template,
                this.accessor,
                omitRule
        );
    }

    @Override
    protected Result<T> missingResult(SourceSpan span) {
        return Result.error("Expected the object to include '%s' as a property."
                .formatted(this.property), span);
    }
}
