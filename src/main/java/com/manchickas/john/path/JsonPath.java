package com.manchickas.john.path;

import com.manchickas.john.exception.JsonException;
import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.path.segment.PathSegment;
import com.manchickas.john.path.segment.SubscriptOperator;
import com.manchickas.john.util.ArrayBuilder;

import java.util.Arrays;

/**
 * Represents a path in an arbitrary JSON structure.
 */
public final class JsonPath {

    private final PathSegment[] segments;
    /**
     * Represents the literal number of segments this path consists of.
     */
    public final int length;
    /**
     * Represents the number of iterations the {@link #traverse(JsonElement)} method would
     * need to perform to traverse the path.
     */
    public final int depth;

    private JsonPath(PathSegment[] segments) {
        this.segments = segments;
        this.length = segments.length;
        this.depth = Arrays.stream(this.segments)
                .mapToInt(PathSegment::depth)
                .sum();
    }

    /**
     * Compiles the provided string into a {@link JsonPath}, wrapping any
     * errors in an unchecked {@link RuntimeException}.
     *
     * @param path the stringified path to compile.
     * @return the compiled {@link JsonPath}.
     */
    public static JsonPath compileUnchecked(String path) {
        try {
            return JsonPath.compile(path);
        } catch (JsonException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Compiles the provided string into a {@link JsonPath}.
     *
     * @param path the path to compile.
     * @return the compiled {@link JsonPath}.
     * @throws JsonException if the provided path was incorrectly formatted.
     */
    public static JsonPath compile(String path) throws JsonException {
        var parser = new PathParser(path);
        return new JsonPath(parser.parse());
    }

    /**
     * Traverses the JSON structure according to the {@link JsonPath}, starting at the provided {@code root}.
     * <br><br>
     * The JSON structure must fully satisfy the one expected by the {@link JsonPath},
     * or else a {@link JsonException} will get thrown.
     *
     * @param root the element to traverse the path relative to.
     * @return the element relative to the provided {@code root}, according to the path.
     * @throws JsonException if the JSON structure doesn't satisfy the path requirements.
     */
    public JsonElement traverse(JsonElement root) throws JsonException {
        for (var segment : this.segments)
            root = segment.resolve(root);
        return root;
    }

    /**
     * Appends the provided {@link JsonPath} to the current one.
     *
     * @param path the path to append.
     * @return the combined path.
     */
    public JsonPath resolve(JsonPath path) {
        return new JsonPath(ArrayBuilder.<PathSegment>builder()
                .appendAll(this.segments)
                .appendAll(path.segments)
                .build(PathSegment[]::new));
    }

    /**
     * Normalizes the path by resolving redundant segments and combining subscript chains, resulting in a path
     * with an equal {@link #depth}, but a potentially lower {@link #length}.
     *
     * @return the normalized path.
     */
    public JsonPath normalize() {
        var builder = ArrayBuilder.<PathSegment>builder();
        for (var segment : this.segments) {
            if (segment == PathSegment.THIS)
                continue;
            if (segment instanceof SubscriptOperator(PathSegment operand, int index)) {
                if (operand == PathSegment.THIS) {
                    var last = builder.trimLastOr(PathSegment.THIS);
                    builder.append(new SubscriptOperator(last, index));
                    continue;
                }
            }
            builder.append(segment);
        }
        return new JsonPath(builder.build(PathSegment[]::new));
    }

    /**
     * Converts the {@link JsonPath} into its string representation,
     * mirroring the syntax needed for compilation.
     *
     * @return the string representation of the path.
     */
    @Override
    public String toString() {
        if (this.length > 0) {
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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof JsonPath path) {
            if (this.length == path.length) {
                for (var i = 0; i < this.length; i++) {
                    var thisSegment = this.segments[i];
                    var otherSegment = path.segments[i];
                    if (thisSegment.equals(otherSegment))
                        continue;
                    return false;
                }
                return true;
            }
            return false;
        }
        return false;
    }
}
