package com.manchickas.john.template.object.constructor;

@FunctionalInterface
public interface UniConstructor<A, T> {

    T construct(A a);
}
