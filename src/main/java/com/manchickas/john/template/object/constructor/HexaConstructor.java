package com.manchickas.john.template.object.constructor;

@FunctionalInterface
public interface HexaConstructor<A, B, C, D, E, F, T> {

    T construct(A a, B b, C c, D d, E e, F f);
}
