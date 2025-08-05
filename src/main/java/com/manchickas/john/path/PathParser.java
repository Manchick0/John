package com.manchickas.john.path;

import com.manchickas.john.exception.JsonException;
import com.manchickas.john.path.segment.PathSegment;
import com.manchickas.john.path.segment.PropertySegment;
import com.manchickas.john.path.segment.SubscriptOperator;
import com.manchickas.john.reader.StringReader;
import com.manchickas.john.util.ArrayBuilder;
import org.jspecify.annotations.Nullable;

public final class PathParser extends StringReader {

    public PathParser(String source) {
        super(source);
    }

    public PathSegment[] parse() throws JsonException {
        if (this.skipWhitespace()) {
            var segments = ArrayBuilder.<PathSegment>builder();
            if (this.peek() == '.') {
                this.read();
                segments.append(this.process(PathSegment.THIS));
            }
            if (this.skipWhitespace()) {
                PathSegment segment;
                while ((segment = this.nextSegment()) != null)
                    segments.append(segment);
            }
            return segments.build(PathSegment[]::new);
        }
        throw new JsonException("Encountered an empty path expression.");
    }

    @Nullable
    private PathSegment nextSegment() throws JsonException {
        if (this.skipWhitespace()) {
            var c = this.peek();
            if (c == '"') {
                this.read();
                return this.process(this.readQuoted('"'));
            }
            if (c == '\'') {
                this.read();
                return this.process(this.readQuoted('\''));
            }
            return this.process(this.readUnquoted());
        }
        return null;
    }

    private PropertySegment readUnquoted() throws JsonException {
        var builder = new StringBuilder();
        while (this.canRead()) {
            var c = this.peek();
            if (c == '\\') {
                this.read();
                builder.append(this.readEscaped());
                continue;
            }
            if (c == '.') {
                throw new JsonException("Encountered an out-of-place relatively prefix. Consider escaping it with '\\\\' to use as a part of the property name.")
                        .withSpan(this.charSpan());
            }
            if (c == '/' || c == '[')
                break;
            builder.appendCodePoint(c);
            this.read();
        }
        var property = builder.toString()
                .stripTrailing();
        if (property.isEmpty())
            throw new JsonException("Encountered an empty unquoted property segment.")
                    .withSpan(this.relativeSpan(1, 0));
        return new PropertySegment(property);
    }

    private PropertySegment readQuoted(char quote) throws JsonException {
        var builder = new StringBuilder();
        while (this.canRead()) {
            var c = this.read();
            if (c == '\\') {
                if (this.peek() == quote) {
                    builder.append(quote);
                    this.read();
                    continue;
                }
                // All escapes within quoted segments are redundant, but
                // it would be really inconsistent if they were disallowed.
                builder.append(this.readEscaped());
                continue;
            }
            if (c == quote) {
                var property = builder.toString();
                if (property.isEmpty())
                    throw new JsonException("Encountered an empty quoted property segment.")
                            .withSpan(this.relativeSpan(2, 0));
                return new PropertySegment(property);
            }
            builder.appendCodePoint(c);
        }
        throw new JsonException("Encountered an unterminated quoted property segment.");
    }

    public PathSegment process(PathSegment segment) throws JsonException {
        if (this.skipWhitespace()) {
            var c = this.read();
            if (c == '/')
                return segment;
            if (c == '[') {
                var i = 0;
                while (this.canRead()) {
                    var d = this.read();
                    if (StringReader.isDigit(d)) {
                        i = i * 10 + (d - '0');
                        continue;
                    }
                    if (d == ']') {
                        var subscript = new SubscriptOperator(segment, i);
                        return this.process(subscript);
                    }
                    throw new JsonException("Encountered an invalid index digit within a subscript operator '%c'.", d)
                            .withSpan(this.relativeSpan(1, 0));
                }
                throw new JsonException("Encountered an unterminated subscript operator.");
            }
            throw new JsonException("Encountered an unexpected character '%c' after a property segment.", c)
                    .withSpan(this.relativeSpan(1, 0));
        }
        return segment;
    }

    private char readEscaped() throws JsonException {
        var c = this.read();
        return switch (c) {
            case '.' -> '.';
            case '/' -> '/';
            case '\\' -> '\\';
            case '[' -> '[';
            default -> throw new JsonException("Encountered an unknown escape sequence '\\%c'.", c)
                    .withSpan(this.relativeSpan(2, 0));
        };
    }
}
