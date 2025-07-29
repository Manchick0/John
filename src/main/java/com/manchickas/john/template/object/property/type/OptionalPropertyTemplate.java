package com.manchickas.john.template.object.property.type;

import com.manchickas.john.position.SourceSpan;
import com.manchickas.john.template.OptionalTemplate;
import com.manchickas.john.template.Result;
import com.manchickas.john.template.Template;
import com.manchickas.john.template.object.property.PropertyAccessor;
import com.manchickas.john.template.object.property.PropertyTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import java.util.function.Supplier;

public final class OptionalPropertyTemplate<Instance, T> extends PropertyTemplate<Instance, T, OptionalPropertyTemplate<Instance, T>> {

    @NotNull
    private final Supplier<@Nullable T> supplier;

    public OptionalPropertyTemplate(String property,
                                    Template<T> template,
                                    PropertyAccessor<Instance, T> accessor,
                                    Predicate<T> omitRule,
                                    @NotNull Supplier<@Nullable T> supplier) {
        super(property, template, accessor, omitRule);
        this.supplier = supplier;
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
    public OptionalPropertyTemplate<Instance, T> omitWhen(Predicate<T> omitRule) {
        return new OptionalPropertyTemplate<>(
                this.property,
                this.template,
                this.accessor,
                omitRule,
                this.supplier
        );
    }

    @Override
    protected Result<T> missingResult(SourceSpan span) {
        return Result.success(this.supplier.get());
    }
}
