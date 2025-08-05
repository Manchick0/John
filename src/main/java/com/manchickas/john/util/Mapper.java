package com.manchickas.john.util;

import com.manchickas.john.position.SourceSpan;
import com.manchickas.john.template.Result;

import java.util.function.Supplier;

@FunctionalInterface
public interface Mapper<T, V> {

    V map(T value) throws Throwable;

    default Result<V> mapAndWrap(T value, Supplier<SourceSpan> span) {
        try {
            return Result.success(this.map(value));
        } catch (Throwable t) {
            if (t instanceof Error e)
                throw e;
            var message = t.getMessage();
            return message != null
                    ? Result.error(t.getMessage(), span.get())
                    : Result.mismatch();
        }
    }
}
