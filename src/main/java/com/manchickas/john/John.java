package com.manchickas.john;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.exception.JsonException;
import com.manchickas.john.parser.Parser;
import com.manchickas.john.reader.StringReader;
import com.manchickas.john.template.Template;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Represents the primary way to interact with the John library.
 */
public final class John {

    public static JsonElement parse(String source) throws JsonException {
        var parser = new Parser(source);
        return parser.parse();
    }

    public static <T> T parse(String source, Template<T> template) throws JsonException {
        return John.parse(source).as(template);
    }

    public static JsonElement parse(Path path) throws JsonException, IOException {
        try(var reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            var builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                builder.append(line)
                        .append('\n');
            return John.parse(builder.toString());
        }
    }

    public static <T> T parse(Path path, Template<T> template) throws JsonException, IOException {
        return John.parse(path).as(template);
    }

    public static <T> JsonElement serialize(T element, Template<T> template) throws JsonException {
        var result = template.wrapSerializeMismatch(element);
        if (result.isSuccess())
            return result.unwrap();
        throw new JsonException(result.message())
                .withSpan(result.span());
    }

    public static <T> String stringify(T element, Template<T> template) throws JsonException {
        return John.stringify(element, template, 0);
    }

    public static <T> String stringify(T element, Template<T> template, int indentation) throws JsonException {
        return John.stringify(John.serialize(element, template), indentation);
    }

    public static String stringify(JsonElement element) {
        return John.stringify(element, 0);
    }

    public static String stringify(JsonElement element, int indentation) {
        var pattern = element.stringifyPattern();
        var reader = new StringReader(pattern);
        var buffer = new StringBuilder();
        var depth = 0;
        while (reader.canRead()) {
            var c = reader.read();
            if (c == '\\' && reader.canRead()) {
                var d = reader.read();
                if (StringReader.isSign(d)) {
                    var e = reader.read();
                    if (e == 'n') {
                        if (d == '+') {
                            John.appendNewLine(buffer, indentation, ++depth);
                            continue;
                        }
                        John.appendNewLine(buffer, indentation, --depth);
                        continue;
                    }
                    var span = reader.relativeSpan(2, 0);
                    throw new IllegalStateException('\n' + span.underlineSource(false) +
                            '\n' + span + ' ' + "Expected an 'n' to follow a sign escape.");
                }
                if (d == 's') {
                    if (indentation > 0)
                        buffer.append(' ');
                    continue;
                }
                if (d == 'n') {
                    John.appendNewLine(buffer, indentation, depth);
                    continue;
                }
                var span = reader.relativeSpan(2, 0);
                throw new IllegalStateException('\n' + span.underlineSource(false) +
                        '\n' + span + ' ' + "Encountered an unknown escape sequence '%s'.".formatted(d));
            }
            buffer.appendCodePoint(c);
        }
        return buffer.toString();
    }

    @CanIgnoreReturnValue
    private static StringBuilder appendNewLine(StringBuilder buffer, int indentation, int depth) {
        if (indentation > 0) {
            buffer.append('\n');
            if (depth > 0)
                buffer.append(" ".repeat(indentation * depth));
        }
        return buffer;
    }
}
