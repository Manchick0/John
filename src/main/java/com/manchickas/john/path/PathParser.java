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
        var builder = ArrayBuilder.<PathSegment>builder();
        PathSegment segment;
        while ((segment = this.nextSegment()) != null)
            builder.append(segment);
        return builder.build(PathSegment[]::new);
    }

    @Nullable
    public PathSegment nextSegment() throws JsonException {
        if (this.canRead()) {
            if (this.peek() == '.') {
                this.read();
                return this.processTrailing(PathSegment.THIS);
            }
            return this.readPropertySegment();
        }
        return null;
    }

    private PathSegment readPropertySegment() throws JsonException {
        var builder = new StringBuilder();
        while (this.canRead()) {
            var c = this.peek();
            if (c == '/' || c == '[')
                break;
            builder.appendCodePoint(c);
            this.read();
        }
        return this.processTrailing(new PropertySegment(builder.toString()));
    }

    private PathSegment processTrailing(PathSegment segment) throws JsonException {
        if (this.canRead()) {
            if (this.peek() == '[') {
                this.read();
                return this.processSubscript(segment);
            }
            if (this.peek() == '/') {
                this.read();
                return segment;
            }
            throw new JsonException("Expected either a segment separator or a subscript operator.")
                    .withSpan(this.charSpan());
        }
        return segment;
    }

    private PathSegment processSubscript(PathSegment operand) throws JsonException {
        if (StringReader.isDigit(this.peek())) {
            var index = this.readPosInteger();
            if (this.peek() == ']') {
                this.read();
                return this.processTrailing(new SubscriptOperator(operand, index));
            }
            throw new JsonException("Expected a closing bracket.")
                    .withSpan(this.charSpan());
        }
        throw new JsonException("Expected an unsigned positive integer.")
                .withSpan(this.charSpan());
    }

    private int readPosInteger() {
        var builder = new StringBuilder();
        while (this.canRead()) {
            var c = this.peek();
            if (StringReader.isDigit(c)) {
                builder.appendCodePoint(c);
                this.read();
                continue;
            }
            break;
        }
        return Integer.parseInt(builder.toString());
    }
}
