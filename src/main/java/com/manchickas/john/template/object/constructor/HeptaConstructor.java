package com.manchickas.john.template.object.constructor;

@FunctionalInterface
public interface HeptaConstructor<A, B, C, D, E, F, H, T> {

    T construct(A a, B b, C c, D d, E e, F f, H h);
}
