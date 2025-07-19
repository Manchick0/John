package com.manchickas.john.template.object.constructor;

@FunctionalInterface
public interface OctaConstructor<A, B, C, D, E, F, H, I, T> {

    T construct(A a, B b, C c, D d, E e, F f, H h, I i);
}
