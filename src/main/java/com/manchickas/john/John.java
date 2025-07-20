package com.manchickas.john;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.exception.JsonException;
import com.manchickas.john.parser.Parser;
import com.manchickas.john.position.SourceSpan;
import com.manchickas.john.reader.StringReader;
import com.manchickas.john.template.Template;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The {@code John} class provides a collection of {@code public static} methods for various JSON-related tasks,
 * ranging from {@linkplain #parse(String) parsing} to {@linkplain #serialize(Object, Template) serialization} and {@linkplain #stringify(JsonElement, int) stringification}.
 * <br><br>
 * The {@code John} class is intended as the main way to interact with the John library.
 * @since 1.0.0
 */
public final class John {

    private John() {}

    /**
     * Attempts to parse the provided {@code source} into an arbitrary {@link JsonElement}.
     * <br><br>
     * The constructed element and its children will keep track of their {@link com.manchickas.john.position.SourceSpan} in
     * the provided {@code source}.
     *
     * @param source the source containing the JSON to parse.
     * @return the parsed {@link JsonElement}.
     * @throws JsonException if the {@code source} contains any invalid JSON.
     * @since 1.0.0
     */
    @NotNull
    public static JsonElement parse(String source) throws JsonException {
        var parser = new Parser(source);
        return parser.parse();
    }

    /**
     * Attempts to parse the provided {@code source} into a typed value, based on the provided {@link Template}
     * <br><br>
     * The process is functionally identical to calling the {@link JsonElement#expect(Template)} method on a parsed {@link JsonElement}.
     *
     * @param source the source containing the JSON to parse.
     * @param template the template the JSON must satisfy.
     * @return the parsed from the {@link JsonElement} value.
     * @throws JsonException if the {@code source} contains any invalid JSON, or if the parsed JSON doesn't satisfy the provided {@code template}.
     * @since 1.0.0
     */
    @NotNull
    public static <T> T parse(String source, Template<T> template) throws JsonException {
        return John.parse(source).expect(template);
    }

    /**
     * Reads the file at the provided {@code path} and attempts to parse its contents into an arbitrary {@link JsonElement}.
     *
     * @param path the path to read the file from.
     * @return the parsed {@link JsonElement}.
     * @throws JsonException if the file contains any invalid JSON.
     * @throws IOException if any I/O occurred while reading the file.
     * @since 1.0.0
     */
    @NotNull
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

    /**
     * Reads the file at the provided {@code path} and attempts to parse its contents into a typed value, based on the provided {@link Template}.
     * <br><br>
     * The process is functionally identical to calling the {@link JsonElement#expect(Template)} method on a parsed {@link JsonElement}.
     *
     * @param path the path to read the file from.
     * @param template the template the JSON must satisfy.
     * @return the parsed from the {@link JsonElement} value.
     * @throws JsonException if the file contains any invalid JSON.
     * @throws IOException if any I/O occurred while reading the file.
     * @since 1.0.0
     */
    @NotNull
    public static <T> T parse(Path path, Template<T> template) throws JsonException, IOException {
        return John.parse(path).expect(template);
    }

    /**
     * Attempts to serialize the provided {@code element} to a {@link JsonElement} according to the provided {@link Template}.
     *
     * @param element the element to serialize.
     * @param template the template the element must satisfy.
     * @return the serialized {@link JsonElement}.
     * @throws JsonException if the {@code element} doesn't satisfy the provided {@link Template}.
     * @since 1.0.0
     */
    @NotNull
    public static <T> JsonElement serialize(T element, Template<T> template) throws JsonException {
        var result = template.serializeAndPromote(element);
        if (result.isSuccess())
            return result.unwrap();
        throw new JsonException(result.message())
                .withSpan(result.span());
    }

    /**
     * Stringifies the provided {@code element} into a <b>minified</b> JSON string, by first
     * {@link #serialize(Object, Template) serializing} it according to the provided {@link Template},
     * and then delegating to the {@link JsonElement} stringification.
     *
     * @param element the element to stringify.
     * @param template the {@link Template} the {@code element} must satisfy.
     * @return the minified string representation of the {@code element}.
     * @throws JsonException if the {@code element} doesn't satisfy the provided {@link Template}.
     */
    @NotNull
    public static <T> String stringify(T element, Template<T> template) throws JsonException {
        return John.stringify(element, template, 0);
    }

    /**
     * Stringifies the provided {@code element} into a properly formatted JSON string, by
     * first {@link #serialize(Object, Template) serializing} it according to the provided {@link Template},
     * and then delegating to {@link JsonElement} stringification.
     *
     * @param element the element to stringify.
     * @param template the {@link Template} the {@code element} must satisfy.
     * @param indentation the number of spaces per nesting level.
     * @return the string representation of the {@code element}.
     * @throws JsonException if the {@code element} doesn't satisfy the provided {@link Template}.
     */
    @NotNull
    public static <T> String stringify(T element, Template<T> template, int indentation) throws JsonException {
        return John.stringifyPattern(John.serialize(element, template)
                .stringifyPattern(), indentation);
    }

    /**
     * Stringifies the provided {@link JsonElement} into a <b>minified</b> JSON string.
     *
     * @param element the {@link JsonElement} to stringify.
     * @return the minified string representation of the {@link JsonElement}.
     * @since 1.0.0
     */
    @NotNull
    public static String stringify(JsonElement element) {
        return John.stringify(element, 0);
    }

    /**
     * Stringifies the provided {@link JsonElement} into a properly formatted JSON string.
     * <br><br>
     * The process is functionally identical to {@linkplain #stringifyPattern(String, int) stringifying} the pattern
     * returned by {@link JsonElement#stringifyPattern()}.
     *
     * @param element the {@link JsonElement} to stringify.
     * @param indentation the number of spaces per nesting level.
     * @return the string representation of the {@link JsonElement}.
     * @since 1.0.0
     */
    @NotNull
    public static String stringify(JsonElement element, int indentation) {
        try {
            return John.stringifyPattern(element.stringifyPattern(), indentation);
        } catch (JsonException e) {
            throw new AssertionError("A stringify pattern returned by a JsonElement (%s) was incorrectly formatted."
                    .formatted(element.getClass().getSimpleName()), e);
        }
    }

    /**
     * Converts the provided {@code pattern} into a <b>minified</b> JSON string.
     *
     * @param pattern the stringify pattern to convert.
     * @return the minified string representation of the {@code pattern}.
     * @throws JsonException if the pattern contains any invalid escape sequences.
     * @see #stringifyPattern(String, int)
     * @since 1.0.0
     */
    @NotNull
    public static String stringifyPattern(String pattern) throws JsonException {
        return John.stringifyPattern(pattern, 0);
    }

    /**
     * Performs the necessary substitutions required to convert a {@linkplain JsonElement#stringifyPattern() stringify pattern}
     * into a properly formatted JSON string.
     * <br><br>
     * While this method is used internally for most stringification logic, it <b>is</b> intended for
     * public usage in cases where you need to hack together some JSON manually.
     *
     * <pre>{@code
     *      var pattern = """
     *          {\\+n"key":\\s[\\+n"foo",\\n"bar"\\-n]\\-n}
     *      """;
     *      var json = John.stringifyPattern(pattern, 4);
     *      System.out.println(json);
     * }</pre>
     *
     * @param pattern the stringify pattern to convert.
     * @param indentation the number of spaces per nesting level.
     * @return the properly formatted JSON string.
     * @throws JsonException if the pattern contains any invalid escape sequences.
     * @since 1.0.0
     */
    @NotNull
    public static String stringifyPattern(String pattern, int indentation) throws JsonException {
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
                    throw new JsonException("Expected an 'n' to follow a sign escape.")
                            .withSpan(span);
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
                throw new JsonException("Encountered an unknown escape sequence '%s'.", d)
                        .withSpan(span);
            }
            if (StringReader.isWhitespace(c))
                continue;
            buffer.appendCodePoint(c);
        }
        if (depth != 0)
            throw new JsonException("Attempted to stringify an asymmetrical pattern.")
                    .withSpan(SourceSpan.lineWide(pattern, 1));
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
