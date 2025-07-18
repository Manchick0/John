package com.manchickas.john.lexer;

import com.manchickas.john.exception.JsonException;
import com.manchickas.john.lexer.lexeme.Lexeme;
import com.manchickas.john.lexer.lexeme.LexemeType;
import com.manchickas.john.reader.StringReader;
import org.jspecify.annotations.Nullable;

import java.util.Set;

public final class Lexer extends StringReader {

    private static final Set<Integer> INDENTATION = Set.of((int) ' ', (int) '\t', (int) '\r', (int) '\n');
    private static final Set<Integer> SEPARATORS = Set.of((int) ',', (int) ':', (int) '{', (int) '}', (int) '[', (int) ']');
    private static final Set<Integer> ESCAPABLE = Set.of((int) '"', (int) '\\', (int) '/', (int) 'b', (int) 'f', (int) 'n', (int) 'r', (int) 't');
    private static final Set<String> BOOLEANS = Set.of("true", "false");

    public Lexer(String source) {
        super(source);
    }

    @Nullable
    public Lexeme<?> nextLexeme() throws JsonException {
        if (this.canRead()) {
            var c = this.peek();
            if (INDENTATION.contains(c)) {
                this.read();
                return this.nextLexeme();
            }
            if (SEPARATORS.contains(c)) {
                this.read();
                return new Lexeme<>(LexemeType.SEPARATOR, (char) c,
                        this.relativeSpan(1, 0));
            }
            if (c == '"')
                return this.readString();
            if (StringReader.isDigit(c) || (c == '-' && StringReader.isDigit(this.peekAhead(1))))
                return this.readNumber();
            return this.readGenericLexeme();
        }
        return null;
    }

    private Lexeme<?> readGenericLexeme() throws JsonException {
        this.pushStamp();
        while (this.canRead()) {
            var d = this.peek();
            if (INDENTATION.contains(d) || SEPARATORS.contains(d) || d == '"')
                break;
            this.read();
        }
        var span = this.span(this.peekStamp());
        var lexeme = this.slice();
        if (lexeme.equals("null"))
            return new Lexeme<>(LexemeType.NULL, null, span);
        if (BOOLEANS.contains(lexeme))
            return new Lexeme<>(LexemeType.BOOLEAN,
                    lexeme.length() == 4, span);
        throw new JsonException("Unexpected lexeme '%s'.", lexeme)
                .withSpan(span);
    }

    private Lexeme<Number> readNumber() throws JsonException {
        var readingDecimal = false;
        var readingExponent = false;
        this.pushStamp();
        if (this.peek() == '-')
            this.read();
        while (this.canRead()) {
            var c = this.peek();
            if (c == '.') {
                if (!readingDecimal) {
                    readingDecimal = true;
                    this.read();
                    continue;
                }
                throw new JsonException("Encountered an out-of-place decimal dot.")
                        .withSpan(this.charSpan());
            }
            if (c == 'e' || c == 'E') {
                if (!readingExponent) {
                    readingExponent = true;
                    this.read();
                    if (StringReader.isSign(this.peek()))
                        this.read();
                    continue;
                }
                throw new JsonException("Encountered an out-of-place exponent.")
                        .withSpan(this.charSpan());
            }
            if (StringReader.isDigit(c)) {
                this.read();
                continue;
            }
            break;
        }
        var span = this.span(this.peekStamp());
        var lexeme = this.slice();
        try {
            var number = Double.parseDouble(lexeme);
            return new Lexeme<>(LexemeType.NUMBER, number, span);
        } catch (NumberFormatException e) {
            throw new JsonException("Encountered an invalid number literal '%s'.", lexeme)
                    .withSpan(span);
        }
    }

    private Lexeme<String> readString() throws JsonException {
        this.pushStamp();
        this.read(); // Consume the quote
        var builder = new StringBuilder();
        while (this.canRead()) {
            var c = this.peek();
            if (c == '\\') {
                this.read();
                var escape = this.read();
                if (escape == 'u') {
                    builder.append(this.readHexCharacter());
                    continue;
                }
                if (ESCAPABLE.contains(escape)) {
                    builder.appendCodePoint(escape);
                    continue;
                }
                throw new JsonException("Encountered an unknown escape sequence '\\%c'.", escape)
                        .withSpan(this.relativeSpan(2, 0));
            }
            if (c == '"') {
                this.read();
                var lexeme = builder.toString();
                return new Lexeme<>(LexemeType.STRING, lexeme, this.span());
            }
            builder.appendCodePoint(c);
            this.read();
        }
        throw new JsonException("Encountered an unterminated string literal.");
    }

    private char readHexCharacter() throws JsonException {
        var result = 0;
        for (var i = 0; i < 4; i++) {
            if (this.canRead()) {
                var c = this.peek();
                if (StringReader.isHexDigit(c)) {
                    var digit = Character.digit(c, 16);
                    result |= digit << ((3 - i) * 4);
                    this.read();
                    continue;
                }
                throw new JsonException("Encountered an invalid hex digit '%c'.", c)
                        .withSpan(this.charSpan());
            }
            throw new JsonException("Encountered an unterminated string literal.");
        }
        return (char) result;
    }
}
