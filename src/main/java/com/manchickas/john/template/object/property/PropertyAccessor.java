package com.manchickas.john.template.object.property;

/**
 * Defines a way to access a specific property on an instance.
 *
 * @param <Instance> the type of the instance on which the property is present.
 * @param <Property> the type of the property being accessed.
 */
@FunctionalInterface
public interface PropertyAccessor<Instance, Property> {

    Property access(Instance instance);
}
