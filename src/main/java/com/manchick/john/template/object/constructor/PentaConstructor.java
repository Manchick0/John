package com.manchick.john.template.object.constructor;

@FunctionalInterface
public interface PentaConstructor<A, B, C, D, E, T> {

    T construct(A a, B b, C c, D d, E e);
}
