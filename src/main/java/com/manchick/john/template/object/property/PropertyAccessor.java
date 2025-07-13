package com.manchick.john.template.object.property;

@FunctionalInterface
public interface PropertyAccessor<T, A> {

    A access(T instance);
}
