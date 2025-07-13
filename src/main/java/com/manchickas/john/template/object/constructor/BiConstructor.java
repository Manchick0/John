package com.manchickas.john.template.object.constructor;

@FunctionalInterface
public interface BiConstructor<A, B, T> {

    T construct(A a, B b);
}
