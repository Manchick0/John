package com.manchickas.john.lexer.lexeme;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.manchickas.john.exception.JsonException;
import com.manchickas.john.position.SourceSpan;
import org.jetbrains.annotations.Contract;

public record Lexeme<T>(LexemeType<T> type, T value,
                        SourceSpan span) {

    public boolean isOf(LexemeType<?> type) {
        return this.type == type;
    }

    public <V> boolean isOf(LexemeType<V> type, V value) {
        return this.type.parse(value)
                .map(v -> v.equals(this.value))
                .orElse(false);
    }

    public <V> V expect(LexemeType<V> type) throws JsonException {
        return type.parse(this.value)
                .orElseThrow(() -> new JsonException("Expected a lexeme of type '%s'", type)
                    .withSpan(this.span));
    }

    @CanIgnoreReturnValue
    @Contract("_, _ -> param2")
    public <V> V expect(LexemeType<V> type, V value) throws JsonException {
        if (this.isOf(type, value))
            return value;
        throw new JsonException("Expected a lexeme of type '%s' with value '%s'", type, value)
                .withSpan(this.span);
    }
}
