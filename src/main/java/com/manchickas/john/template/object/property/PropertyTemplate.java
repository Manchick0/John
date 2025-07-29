package com.manchickas.john.template.object.property;

import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.ast.JsonObject;
import com.manchickas.john.exception.JsonException;
import com.manchickas.john.position.SourceSpan;
import com.manchickas.john.template.Template;
import com.manchickas.john.template.Result;
import com.manchickas.john.template.object.property.type.OptionalPropertyTemplate;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class PropertyTemplate<Instance, T, Self extends PropertyTemplate<Instance, T, Self>> implements Template<T> {

    protected final String property;
    protected final Template<T> template;
    protected final PropertyAccessor<Instance, T> accessor;
    protected final Predicate<@Nullable T> omitRule;

    public PropertyTemplate(String property,
                            Template<T> template,
                            PropertyAccessor<Instance, T> accessor,
                            Predicate<T> omitRule) {
        this.property = property;
        this.template = template;
        this.accessor = accessor;
        this.omitRule = omitRule;
    }

    /**
     * Composes a {@link Template} that supplies {@code null} if the necessary property isn't present on the object.
     *
     * @return a {@link Template} that yields the current template's value, or {@code null} if the property wasn't present.
     * @since 1.2.0
     */
    @Override
    public OptionalPropertyTemplate<Instance, T> optional() {
        return this.optional(() -> null);
    }

    @Override
    public abstract OptionalPropertyTemplate<Instance, T> optional(@NotNull Supplier<@Nullable T> supplier);

    public Self omitNulls() {
        return this.omitWhen(Objects::isNull);
    }

    public abstract Self omitWhen(Predicate<@Nullable T> omitRule);

    public Optional<Result<JsonElement>> serializeProperty(Instance instance) {
        var value = this.access(instance);
        if (this.omitRule.test(value))
            return Optional.empty();
        return Optional.of(this.serialize(value));
    }

    @Override
    public Result<T> parse(JsonElement element) {
        if (element instanceof JsonObject object) {
            try {
                var prop = object.property(this.property);
                return this.template.parseAndPromote(prop);
            } catch (JsonException e) {
                return this.missingResult(element.span());
            }
        }
        return Result.mismatch();
    }

    protected abstract Result<T> missingResult(SourceSpan span);

    @Override
    public Result<JsonElement> serialize(T value) {
        return this.template.serializeAndPromote(value);
    }

    @Override
    public String name(IntSet encountered) {
        return this.property + ": " + this.template.name(encountered);
    }

    public T access(Instance instance) {
        return this.accessor.access(instance);
    }

    public String property() {
        return this.property;
    }
}
