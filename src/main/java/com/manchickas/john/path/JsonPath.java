package com.manchickas.john.path;

import com.manchickas.john.exception.JsonException;
import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.path.segment.PathSegment;
import com.manchickas.john.path.segment.SubscriptOperator;
import com.manchickas.john.util.ArrayBuilder;

public final class JsonPath {

    private final PathSegment[] segments;

    JsonPath(PathSegment[] segments) {
        this.segments = segments;
    }

    public static JsonPath compile(String path) {
        try {
            var parser = new PathParser(path);
            return new JsonPath(parser.parse());
        } catch (JsonException e) {
            throw new RuntimeException('\n' + e.getMessage());
        }
    }

    public JsonElement traverse(JsonElement root) throws JsonException {
        for (var segment : this.segments)
            root = segment.resolve(root);
        return root;
    }

    public JsonPath resolve(JsonPath path) {
        return new JsonPath(ArrayBuilder.<PathSegment>builder()
                .appendAll(this.segments)
                .appendAll(path.segments)
                .build(PathSegment[]::new));
    }

    public JsonPath normalize() {
        var builder = ArrayBuilder.<PathSegment>builder();
        for (var segment : this.segments) {
            if (segment == PathSegment.THIS)
                continue;
            if (segment instanceof SubscriptOperator(PathSegment operand, int index)) {
                if (operand == PathSegment.THIS) {
                    var last = builder.trimLast();
                    builder.append(new SubscriptOperator(last, index));
                    continue;
                }
            }
            builder.append(segment);
        }
        return new JsonPath(builder.build(PathSegment[]::new));
    }

    public boolean isEmpty() {
        return this.length() == 0;
    }

    public int length() {
        return this.segments.length;
    }

    @Override
    public String toString() {
        if (this.length() > 0) {
            var builder = new StringBuilder();
            for (var i = 0; i < this.segments.length; i++) {
                var segment = this.segments[i];
                if (i > 0)
                    builder.append('/');
                builder.append(segment);
            }
            return builder.toString();
        }
        return ".";
    }
}
