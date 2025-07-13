package com.manchickas.john.template.object.constructor;

@FunctionalInterface
public interface TetraConstructor<A, B, C, D, T> {

    T construct(A a, B b, C c, D d);
}
