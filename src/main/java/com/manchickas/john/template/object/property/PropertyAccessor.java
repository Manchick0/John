package com.manchickas.john.template.object.property;

@FunctionalInterface
public interface PropertyAccessor<T, A> {

    A access(T instance);
}
