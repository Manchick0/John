package com.manchickas.john.template.object.constructor;

@FunctionalInterface
public interface TriConstructor<A, B, C, T> {

    T construct(A a, B b, C c);
}
