package com.manchickas.john.template.object.property;

@FunctionalInterface
public interface PropertyAccessor<Instance, Property> {

    Property access(Instance instance);
}
