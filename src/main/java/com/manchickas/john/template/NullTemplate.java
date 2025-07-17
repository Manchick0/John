package com.manchickas.john.template;

public interface NullTemplate extends Template<Void> {

    default <V> Template<V> cast() {
        return this.map(__ -> null, __ -> null);
    }
}
