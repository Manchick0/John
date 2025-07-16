package com.manchickas.john.lexer.lexeme;

import java.util.Optional;

public interface LexemeType<T> {

    LexemeType<Character> SEPARATOR = (obj) -> {
        if (obj instanceof Character c)
            return Optional.of(c);
        return Optional.empty();
    };
    LexemeType<String> STRING = (obj) -> {
        if (obj instanceof String s)
            return Optional.of(s);
        return Optional.empty();
    };
    LexemeType<Number> NUMBER = (obj) -> {
        if (obj instanceof Number n)
            return Optional.of(n);
        return Optional.empty();
    };
    LexemeType<Boolean> BOOLEAN = (obj) -> {
        if (obj instanceof Boolean b)
            return Optional.of(b);
        return Optional.empty();
    };
    LexemeType<Void> NULL = (obj) -> Optional.empty();

    Optional<T> parse(Object other);
}
