package com.manchickas.john.parser;

import com.google.common.collect.ImmutableMap;
import com.manchickas.john.ast.JsonArray;
import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.ast.JsonObject;
import com.manchickas.john.ast.primitive.JsonBoolean;
import com.manchickas.john.ast.primitive.JsonNull;
import com.manchickas.john.ast.primitive.JsonNumber;
import com.manchickas.john.ast.primitive.JsonString;
import com.manchickas.john.exception.JsonException;
import com.manchickas.john.lexer.Lexer;
import com.manchickas.john.lexer.lexeme.Lexeme;
import com.manchickas.john.lexer.lexeme.LexemeType;
import com.manchickas.john.position.SourceSpan;
import com.manchickas.john.util.ArrayBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import org.jetbrains.annotations.NotNull;

public final class Parser {

    private final Lexer lexer;
    private final ObjectArrayFIFOQueue<Lexeme<?>> buffer;

    public Parser(String source) {
        this.lexer = new Lexer(source);
        this.buffer = new ObjectArrayFIFOQueue<>(8);
    }

    @NotNull
    public JsonElement parse() throws JsonException {
        if (this.canRead()) {
            var lexeme = this.peek();
            var span = lexeme.span();
            if (lexeme.isOf(LexemeType.SEPARATOR, '{'))
                return this.parseObject(span);
            if (lexeme.isOf(LexemeType.SEPARATOR, '['))
                return this.parseArray(span);
            if (lexeme.isOf(LexemeType.STRING)) {
                this.read();
                var str = lexeme.expect(LexemeType.STRING);
                return new JsonString(span, str);
            }
            if (lexeme.isOf(LexemeType.NUMBER)) {
                this.read();
                var number = lexeme.expect(LexemeType.NUMBER);
                return new JsonNumber(span, number);
            }
            if (lexeme.isOf(LexemeType.BOOLEAN)) {
                this.read();
                var bool = lexeme.expect(LexemeType.BOOLEAN);
                return new JsonBoolean(span, bool);
            }
            if (lexeme.isOf(LexemeType.NULL)) {
                this.read();
                return new JsonNull(span);
            }
            throw new JsonException("Encountered an unexpected lexeme '%s'.", lexeme.value())
                    .withSpan(span);
        }
        throw new JsonException("Encountered an EOF in place of a JSON element.");
    }

    private JsonObject parseObject(SourceSpan start) throws JsonException {
        this.read().expect(LexemeType.SEPARATOR, '{');
        if (this.canRead()) {
            if (this.peek()
                    .isOf(LexemeType.SEPARATOR, '}')) {
                var l = this.read();
                var span = l.span();
                return new JsonObject(start.extend(span), ImmutableMap.of());
            }
            var builder = ImmutableMap.<String, JsonElement>builder();
            while (this.canRead()) {
                var key = this.read().expect(LexemeType.STRING);
                if (this.canRead()) {
                    this.read().expect(LexemeType.SEPARATOR, ':');
                    builder.put(key, this.parse());
                    if (this.canRead()) {
                        var separator = this.read();
                        if (separator.isOf(LexemeType.SEPARATOR, '}')) {
                            var span = separator.span();
                            return new JsonObject(start.extend(span), builder.buildKeepingLast());
                        }
                        if (separator.isOf(LexemeType.SEPARATOR, ','))
                            continue;
                        throw new JsonException("Expected either a comma or a closing brace.")
                                .withSpan(separator.span());
                    }
                    break;
                }
                break;
            }
        }
        throw new JsonException("Encountered an unterminated object literal.");
    }

    private JsonArray parseArray(SourceSpan start) throws JsonException {
        this.read().expect(LexemeType.SEPARATOR, '[');
        if (this.canRead()) {
            if (this.peek()
                    .isOf(LexemeType.SEPARATOR, ']')) {
                var l = this.read();
                var span = l.span();
                return new JsonArray(start.extend(span), new JsonElement[0]);
            }
            var builder = ArrayBuilder.<JsonElement>builder();
            while (this.canRead()) {
                builder.append(this.parse());
                if (this.canRead()) {
                    var separator = this.read();
                    if (separator.isOf(LexemeType.SEPARATOR, ']')) {
                        var span = separator.span();
                        return new JsonArray(start.extend(span),
                                builder.build(JsonElement[]::new));
                    }
                    if (separator.isOf(LexemeType.SEPARATOR, ','))
                        continue;
                    throw new JsonException("Expected either a comma or a closing bracket.")
                            .withSpan(separator.span());
                }
                break;
            }
        }
        throw new JsonException("Encountered an unterminated array literal.");
    }

    private Lexeme<?> peek() throws JsonException {
        if (this.buffer.isEmpty()) {
            var lexeme = this.lexer.nextLexeme();
            this.buffer.enqueue(lexeme);
            return lexeme;
        }
        return this.buffer.last();
    }

    private Lexeme<?> read() throws JsonException {
        if (this.buffer.isEmpty())
            return this.lexer.nextLexeme();
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
