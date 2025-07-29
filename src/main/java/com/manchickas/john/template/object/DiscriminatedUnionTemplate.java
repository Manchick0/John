package com.manchickas.john.template.object;

import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.ast.JsonObject;
import com.manchickas.john.template.object.property.PropertyTemplate;
import com.manchickas.john.template.Result;
import com.manchickas.john.template.Template;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.function.Function;

public final class DiscriminatedUnionTemplate<Disc, Instance> implements Template<Instance> {

    private final PropertyTemplate<Instance, Disc, ?> discriminator;
    private final Function<Disc, Template<? extends Instance>> resolver;

    public DiscriminatedUnionTemplate(PropertyTemplate<Instance, Disc, ?> discriminator,
                                      Function<Disc, Template<? extends Instance>> resolver) {
        this.discriminator = discriminator;
        this.resolver = resolver;
    }

    @Override
    public Result<Instance> parse(JsonElement element) {
        if (element instanceof JsonObject)
            return this.discriminator.parseAndPromote(element)
                    .flatMap(disc -> this.resolver.apply(disc)
                            .parseAndPromote(element)
                            .map(t -> (Instance) t));
        return Result.mismatch();
    }

    @Override
    public Result<JsonElement> serialize(Instance value) {
        if (value != null) {
            var discriminator = this.discriminator.access(value);
            var template = this.resolver.apply(discriminator);
            return this.serializeWith(value, template);
        }
        return Result.mismatch();
    }

    @SuppressWarnings("unchecked")
    private <P extends Instance> Result<JsonElement> serializeWith(Instance instance, Template<P> template) {
        try {
            var unchecked = (P) instance;
            return template.serialize(unchecked)
                    .flatMap(el -> {
                        if (el instanceof JsonObject object) {
                            var name = this.discriminator.property();
                            var prop = this.discriminator.serializeProperty(instance);
                            if (prop.isPresent())
                                return prop.get()
                                        .map(element -> object.with(name, element));
                        }
                        return Result.success(el);
                    });
        } catch (ClassCastException e) {
            return Result.mismatch();
        }
    }

    @Override
    public String name(IntSet encountered) {
        return "{ " + this.discriminator.name(encountered) + ", ... }";
    }
}
