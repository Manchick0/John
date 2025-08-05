package com.manchickas.john.ast.primitive;

import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.position.SourceSpan;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class JsonPrimitive<T> extends JsonElement {

    public JsonPrimitive(@Nullable SourceSpan span) {
        super(span);
    }

    public abstract T value();

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof JsonPrimitive<?> other
                && other.getClass() == this.getClass()) {
            var span = other.span();
            if (span == null || this.span == null || this.span.equals(span))
                return this.value().equals(other.value());
            return false;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.value());
    }

    @Override
    public int length() {
        return 1;
    }
}
