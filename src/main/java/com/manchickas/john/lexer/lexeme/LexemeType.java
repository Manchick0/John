package com.manchickas.john.lexer.lexeme;

import java.util.Optional;

public interface LexemeType<T> {

    LexemeType<Character> SEPARATOR = new LexemeType<>() {

        @Override
        public Optional<Character> parse(Object obj) {
            if (obj instanceof Character c)
                return Optional.of(c);
            return Optional.empty();
        }

        @Override
        public String toString() {
            return "separator";
        }
    };
    LexemeType<String> STRING = new LexemeType<>() {

        @Override
        public Optional<String> parse(Object obj) {
            if (obj instanceof String s)
                return Optional.of(s);
            return Optional.empty();
        }

        @Override
        public String toString() {
            return "string";
        }
    };
    LexemeType<Number> NUMBER = new LexemeType<>() {

        @Override
        public Optional<Number> parse(Object obj) {
            if (obj instanceof Number n)
                return Optional.of(n);
            return Optional.empty();
        }

        @Override
        public String toString() {
            return "number";
        }
    };
    LexemeType<Boolean> BOOLEAN = new LexemeType<>() {
        @Override
        public Optional<Boolean> parse(Object obj) {
            if (obj instanceof Boolean b)
                return Optional.of(b);
            return Optional.empty();
        }

        @Override
        public String toString() {
            return "boolean";
        }
    };
    LexemeType<Void> NULL = new LexemeType<>() {

        @Override
        public Optional<Void> parse(Object obj) {
            return Optional.empty();
        }

        @Override
        public String toString() {
            return "null";
        }
    };

    Optional<T> parse(Object obj);
}
