package com.manchickas.john.template.object;

import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.ast.JsonObject;
import com.manchickas.john.util.Result;
import com.manchickas.john.template.Template;
import com.manchickas.john.template.object.property.PropertyTemplate;

import java.util.function.Function;

public final class DiscriminatedUnion<D, T> implements Template<T> {

    private final PropertyTemplate<T, D> discriminator;
    private final Function<D, Template<? extends T>> resolver;

    public DiscriminatedUnion(PropertyTemplate<T, D> discriminator,
                              Function<D, Template<? extends T>> resolver) {
        this.discriminator = discriminator;
        this.resolver = resolver;
    }

    @Override
    public Result<T> parse(JsonElement element) {
        if (element instanceof JsonObject)
            return this.discriminator.wrapParseMismatch(element)
                    .flatMap(disc -> this.resolver.apply(disc)
                            .wrapParseMismatch(element)
                            .map(t -> (T) t));
        return Result.mismatch();
    }

    @Override
    public Result<JsonElement> serialize(T value) {
        var discriminator = this.discriminator.access(value);
        var template = this.resolver.apply(discriminator);
        return this.serializeWith(value, template);
    }

    @SuppressWarnings("unchecked")
    private <P extends T> Result<JsonElement> serializeWith(T value, Template<P> template) {
        try {
            var unchecked = (P) value;
            return template.serialize(unchecked)
                    .flatMap(el -> {
                        if (el instanceof JsonObject object) {
                            var name = this.discriminator.name;
                            return this.discriminator.serializeProperty(unchecked)
                                    .map(prop -> object.with(name, prop));
                        }
                        return Result.success(el);
                    });
        } catch (ClassCastException e) {
            return Result.mismatch();
        }
    }

    @Override
    public String name() {
        return "{ " + this.discriminator.name() + ", ... }";
    }
}
