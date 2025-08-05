package com.manchickas.john.util;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CheckReturnValue;

import java.util.Arrays;
import java.util.function.IntFunction;

public final class ArrayBuilder<T> {

    private static final int DEFAULT_CAPACITY = 4;

    private T[] buffer;
    private int length;

    @SuppressWarnings("unchecked")
    private ArrayBuilder(final int initialCapacity) {
        this.buffer = (T[]) new Object[initialCapacity];
        this.length = 0;
    }

    public static <T> ArrayBuilder<T> builder() {
        return new ArrayBuilder<>(ArrayBuilder.DEFAULT_CAPACITY);
    }

    public static <T> ArrayBuilder<T> builderWithExpectedSize(final int size) {
        return new ArrayBuilder<>(size);
    }

    @CanIgnoreReturnValue
    public ArrayBuilder<T> append(final T entry) {
        this.ensureFits();
        this.buffer[this.length++] = entry;
        return this;
    }

    @CanIgnoreReturnValue
    public ArrayBuilder<T> appendAll(final ArrayBuilder<T> builder) {
        for (var i = 0; i < builder.length; i++)
            this.append(builder.buffer[i]);
        return this;
    }

    @CanIgnoreReturnValue
    public ArrayBuilder<T> appendAll(final T[] entries) {
        return this.appendAll(entries, 0);
    }

    public ArrayBuilder<T> appendAll(final T[] entries, int offset) {
        for (var i = offset; i < entries.length; i++)
            this.append(entries[i]);
        return this;
    }

    public T trimLast() {
        return this.buffer[--this.length];
    }

    public T trimLastOr(T other) {
        return this.length > 0 ? this.trimLast() : other;
    }

    public boolean isEmpty() {
        return this.length == 0;
    }

    public int length() {
        return this.length;
    }

    @CheckReturnValue
    public T[] build(final IntFunction<T[]> factory) {
        var built = factory.apply(this.length);
        System.arraycopy(this.buffer, 0, built, 0, this.length);
        return built;
    }

    private void ensureFits() {
        if ((this.length) >= this.buffer.length)
            this.buffer = Arrays.copyOf(this.buffer, this.buffer.length * 2);
    }
}