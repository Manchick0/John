package com.manchickas.john.lexer;

import com.manchickas.john.position.SourceSpan;
import org.jetbrains.annotations.NotNull;

public record Lexeme(Type type, String value, SourceSpan span) {

    public boolean isOf(Type type) {
        return this.type == type;
    }

    public boolean isOf(String value) {
        return this.value.equals(value);
    }

    public boolean isOf(char value) {
        return this.value.charAt(0) == value;
    }

    public boolean isOf(Type type, String value) {
        return this.type == type && this.value.equals(value);
    }

    public boolean isOf(Type type, char value) {
        return this.type == type && this.value.charAt(0) == value;
    }

    @NotNull
    @Override
    public String toString() {
        return "(" + this.type + " | " + this.value + ')' + " @" + this.span;
    }

    public enum Type {

        SEPARATOR("separator"),
        STRING("string"),
        NUMBER("number"),
        BOOLEAN("boolean"),
        NULL("null");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
