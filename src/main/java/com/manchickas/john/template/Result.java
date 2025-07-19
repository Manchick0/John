package com.manchickas.john.template;

import com.manchickas.john.position.SourceSpan;

import java.util.NoSuchElementException;
import java.util.function.Function;

/**
 * Represents a state of an operation.
 * <br><br>
 * A single {@code Result} may be in one of three states. It either represents a {@linkplain  Result.Success success},
 * a {@linkplain Result.Mismatch mismatch}, or an {@linkplain Result.Error error}. The state may be inspected with
 * the appropriate {@code isXYZ()} method.
 * <br><br>
 * A {@link Success} result carries a value, which may be retrieved using the {@link #unwrap()} method. An {@link Error}
 * carries a message and the {@link SourceSpan} of the exact {@link com.manchickas.john.ast.JsonElement JsonElement} that caused the error.
 * <br><br>
 * A {@link Mismatch} is used internally by {@link com.manchickas.john.template.Template Template}s to represent an operation
 * that failed to match the current {@link Template}, and can be promoted to an error with {@link #promoteMismatch(String, SourceSpan)}.
 *
 * @param <T> the type of the value carried by a {@link Success successful} {@code Result}.
 */
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

    default Result<T> promoteMismatch(String message, SourceSpan span) {
        return this;
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
        public Result<T> promoteMismatch(String message, SourceSpan span) {
            return Result.error(message, span);
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
