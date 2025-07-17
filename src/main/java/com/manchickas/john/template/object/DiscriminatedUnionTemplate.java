package com.manchickas.john.template.object;

import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.ast.JsonObject;
import com.manchickas.john.template.object.property.PropertyTemplate;
import com.manchickas.john.util.Result;
import com.manchickas.john.template.Template;

import java.util.function.Function;

public final class DiscriminatedUnionTemplate<Disc, Instance> implements Template<Instance> {

    private final PropertyTemplate<Instance, Disc> discriminator;
    private final Function<Disc, Template<? extends Instance>> resolver;

    public DiscriminatedUnionTemplate(PropertyTemplate<Instance, Disc> discriminator,
                                      Function<Disc, Template<? extends Instance>> resolver) {
        this.discriminator = discriminator;
        this.resolver = resolver;
    }

    @Override
    public Result<Instance> parse(JsonElement element) {
        if (element instanceof JsonObject)
            return this.discriminator.wrapParseMismatch(element)
                    .flatMap(disc -> this.resolver.apply(disc)
                            .wrapParseMismatch(element)
                            .map(t -> (Instance) t));
        return Result.mismatch();
    }

    @Override
    public Result<JsonElement> serialize(Instance value) {
        var discriminator = this.discriminator.access(value);
        var template = this.resolver.apply(discriminator);
        return this.serializeWith(value, template);
    }

    @SuppressWarnings("unchecked")
    private <P extends Instance> Result<JsonElement> serializeWith(Instance instance, Template<P> template) {
        try {
            var unchecked = (P) instance;
            return template.serialize(unchecked)
                    .flatMap(el -> {
                        if (el instanceof JsonObject object) {
                            var name = this.discriminator.property();
                            var value = this.discriminator.serializeProperty(instance);
                            return value.map(prop -> object.with(name, prop));
                        }
                        return Result.success(el);
                    });
        } catch (ClassCastException e) {
            return Result.mismatch();
        }
    }

    @Override
    public String name(boolean potentialRecursion) {
        return "{ " + this.discriminator.name() + ", ... }";
    }
}
