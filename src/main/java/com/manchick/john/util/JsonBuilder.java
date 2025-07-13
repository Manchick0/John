package com.manchick.john.util;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.manchick.john.ast.JsonElement;

import java.util.Arrays;

@CanIgnoreReturnValue
public final class JsonBuilder {

    private final int indentation;
    private int[] buffer;
    private int length;
    private int depth;

    public JsonBuilder(int indentation) {
        this.indentation = indentation;
        this.buffer = new int[16];
        this.length = 0;
        this.depth = 0;
    }

    public JsonBuilder appendNull() {
        this.append("null");
        return this;
    }

    public JsonBuilder appendString(String string) {
        this.append('"');
        this.append(string);
        this.append('"');
        return this;
    }

    public JsonBuilder appendLine() {
        if (this.indentation > 0) {
            this.append('\n');
            this.append(" ".repeat(this.depth * this.indentation));
        }
        return this;
    }

    public JsonBuilder appendElement(JsonElement element) {
        element.stringify(this);
        return this;
    }

    public JsonBuilder append(String string) {
        for (var i = 0; i < string.length(); i++) {
            var point = string.codePointAt(i);
            if (point > 0xFFFF)
                i++;
            this.appendPoint(point);
        }
        return this;
    }

    public JsonBuilder append(Number number) {
        var value = number.doubleValue();
        if (Math.floor(value) == value)
            return this.append(Long.toString((long) value));
        return this.append(Double.toString(value));
    }

    public JsonBuilder append(boolean bool) {
        return this.append(bool ? "true" : "false");
    }

    public JsonBuilder append(char c) {
        return this.appendPoint(c);
    }

    public JsonBuilder appendPoint(int point) {
        this.ensureFits();
        this.buffer[this.length++] = point;
        return this;
    }

    public JsonBuilder trimEnd(int amount) {
        this.length -= amount;
        return this;
    }

    public JsonBuilder nest() {
        this.depth++;
        return this;
    }

    public JsonBuilder flatten() {
        this.depth--;
        return this;
    }

    public String build() {
        return new String(this.buffer, 0, this.length);
    }

    @Override
    public String toString() {
        return this.build();
    }

    private void ensureFits() {
        if (this.length >= this.buffer.length)
            this.buffer = Arrays.copyOf(this.buffer, this.buffer.length * 2);
    }
}
