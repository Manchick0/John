package com.manchick.john.util;

import com.manchick.john.position.SourceSpan;
import com.manchick.john.template.Template;

import java.util.NoSuchElementException;
import java.util.function.Function;

public sealed interface Result<T> permits Result.Error, Result.Mismatch, Result.Success {

    static <T> Result<T> success(T value) {
        return new Success<>(value);
    }

    static <T> Result<T> error(String message, SourceSpan span) {
        return new Error<>(message, span);
    }

    @SuppressWarnings("unchecked")
    static <T> Result<T> mismatch() {
        return (Result<T>) Mismatch.INSTANCE;
    }

    default boolean isSuccess() {
        return false;
    }
    default boolean isMismatch() {
        return false;
    }
    default boolean isError() {
        return false;
    }

    T unwrap();
    T unwrapOr(T other);

    String message();
    SourceSpan span();

    @SuppressWarnings("unchecked")
    default <V> Result<V> map(Function<T, V> mapper) {
        if (this.isSuccess())
            return Result.success(mapper.apply(this.unwrap()));
        return (Result<V>) this;
    }

    @SuppressWarnings("unchecked")
    default <V> Result<V> flatMap(Function<T, Result<V>> mapper) {
        if (this.isSuccess())
            return mapper.apply(this.unwrap());
        return (Result<V>) this;
    }

    record Success<T>(T value) implements Result<T> {

        @Override
        public T unwrap() {
            return this.value;
        }

        @Override
        public T unwrapOr(T other) {
            return this.value;
        }

        @Override
        public String message() {
            throw new IllegalStateException("Attempted to access the message on a successful result.");
        }

        @Override
        public SourceSpan span() {
            throw new NoSuchElementException("Attempted to access the span on a successful result.");
        }

        @Override
        public boolean isSuccess() {
            return true;
        }
    }

    record Mismatch<T>() implements Result<T> {

        private static final Mismatch<?> INSTANCE = new Mismatch<>();

        @Override
        public T unwrap() {
            throw new IllegalStateException("Attempted to unwrap a mismatch result.");
        }

        @Override
        public T unwrapOr(T other) {
            return other;
        }

        @Override
        public String message() {
            throw new IllegalStateException("Attempted to access the message on a mismatch result.");
        }

        @Override
        public SourceSpan span() {
            throw new IllegalStateException("Attempted to access the span on a mismatch result.");
        }

        @Override
        public boolean isMismatch() {
            return true;
        }
    }

    record Error<T>(String message, SourceSpan span) implements Result<T> {

        @Override
        public T unwrap() {
            throw new IllegalStateException("Attempted to unwrap an error result.");
        }

        @Override
        public T unwrapOr(T other) {
            return other;
        }

        @Override
        public String message() {
            return this.message;
        }

        @Override
        public boolean isError() {
            return true;
        }
    }
}
