package com.manchickas.john.parser;

import com.google.common.collect.ImmutableMap;
import com.manchickas.john.exception.JsonException;
import com.manchickas.john.ast.JsonArray;
import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.ast.JsonObject;
import com.manchickas.john.ast.primitive.*;
import com.manchickas.john.lexer.Lexeme;
import com.manchickas.john.lexer.Lexer;
import com.manchickas.john.position.SourceSpan;
import com.manchickas.john.util.ArrayBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;

public final class Parser {

    private final Lexer lexer;
    private final ObjectArrayFIFOQueue<Lexeme> buffer;

    public Parser(String source) {
        this.lexer = new Lexer(source);
        this.buffer = new ObjectArrayFIFOQueue<>(8);
    }

    public JsonElement parse() throws JsonException {
        var lexeme = this.peek();
        var span = lexeme.span();
        if (lexeme.isOf(Lexeme.Type.SEPARATOR, '{'))
            return this.parseObject(span);
        if (lexeme.isOf(Lexeme.Type.SEPARATOR, '['))
            return this.parseArray(span);
        if (lexeme.isOf(Lexeme.Type.STRING)) {
            this.read();
            return new JsonString(span, lexeme.value());
        }
        if (lexeme.isOf(Lexeme.Type.NUMBER)) {
            this.read();
            try {
                var number = Double.parseDouble(lexeme.value());
                return new JsonNumber(span, number);
            } catch (NumberFormatException e) {
                throw new JsonException("Encountered an invalid number literal '%s'.", lexeme.value())
                        .withSpan(span);
            }
        }
        if (lexeme.isOf(Lexeme.Type.BOOLEAN)) {
            this.read();
            var bool = lexeme.value();
            return new JsonBoolean(span, bool.length() == 4);
        }
        if (lexeme.isOf(Lexeme.Type.NULL)) {
            this.read();
            return new JsonNull(span);
        }
        throw new JsonException("Encountered an unexpected lexeme '%s'.", lexeme.value())
                .withSpan(span);
    }

    private JsonObject parseObject(SourceSpan start) throws JsonException {
        this.expect(Lexeme.Type.SEPARATOR, "{");
        if (this.peek().isOf(Lexeme.Type.SEPARATOR, "}")) {
            var l = this.read();
            var span = l.span();
            return new JsonObject(start.extend(span), ImmutableMap.of());
        }
        var builder = ImmutableMap.<String, JsonElement>builder();
        while (this.canRead()) {
            var key = this.expect(Lexeme.Type.STRING);
            this.expect(Lexeme.Type.SEPARATOR, ":");
            var value = this.parse();
            builder.put(key, value);
            if (this.peek().isOf(Lexeme.Type.SEPARATOR, "}")) {
                var l = this.read();
                var span = l.span();
                return new JsonObject(start.extend(span), builder.build());
            }
            this.expect(Lexeme.Type.SEPARATOR, ",");
        }
        throw new JsonException("Encountered an unterminated object literal.");
    }

    private JsonArray parseArray(SourceSpan start) throws JsonException {
        this.expect(Lexeme.Type.SEPARATOR, "[");
        if (this.peek().isOf(Lexeme.Type.SEPARATOR, "]")) {
            var l = this.read();
            var span = l.span();
            return new JsonArray(start.extend(span), new JsonElement[0]);
        }
        var builder = ArrayBuilder.<JsonElement>builder();
        while (this.canRead()) {
            var value = this.parse();
            builder.append(value);
            if (this.peek().isOf(Lexeme.Type.SEPARATOR, "]")) {
                var l = this.read();
                var span = l.span();
                return new JsonArray(start.extend(span), builder.build(JsonElement[]::new));
            }
            this.expect(Lexeme.Type.SEPARATOR, ",");
        }
        throw new JsonException("Encountered an unterminated array literal.");
    }

    private String expect(Lexeme.Type type) throws JsonException {
        var lexeme = this.peek();
        if (lexeme.isOf(type))
            return this.read()
                    .value();
        throw new JsonException("Expected a lexeme of type '%s'", type)
                .withSpan(lexeme.span());
    }

    private void expect(Lexeme.Type type, String value) throws JsonException {
        var lexeme = this.peek();
        if (lexeme.isOf(type, value)) {
            this.read();
            return;
        }
        throw new JsonException("Expected a lexeme of type '%s' with value '%s'", type, value)
                .withSpan(lexeme.span());
    }

    private Lexeme peek() throws JsonException {
        if (this.buffer.isEmpty()) {
            var lexeme = this.lexer.nextLexeme();
            this.buffer.enqueue(lexeme);
            return lexeme;
        }
        return this.buffer.last();
    }

    private Lexeme read() {
        return this.buffer.dequeueLast();
    }

    private boolean canRead() throws JsonException {
        if (this.buffer.isEmpty()) {
            var lexeme = this.lexer.nextLexeme();
            if (lexeme == null)
                return false;
            this.buffer.enqueue(lexeme);
            return true;
        }
        return true;
    }
}
