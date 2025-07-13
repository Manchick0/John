package com.manchickas.john;

import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.exception.JsonException;
import com.manchickas.john.parser.Parser;
import com.manchickas.john.template.Template;
import com.manchickas.john.util.JsonBuilder;

/**
 * <b>John</b>: <b>J</b>ohn's <b>O</b>verly <b>H</b>ilarious <b>N</b>ame
 */
public final class John {

    public static JsonElement parse(String source) throws JsonException {
        var parser = new Parser(source);
        return parser.parse();
    }

    public static <T> T parse(String source, Template<T> template) throws JsonException {
        return John.parse(source).as(template);
    }

    public static <T> String stringify(T element, Template<T> template) throws JsonException {
        return John.stringify(element, template, 0);
    }

    public static <T> String stringify(T element, Template<T> template, int indentation) throws JsonException {
        var result = template.wrapSerializeMismatch(element);
        if (result.isSuccess())
            return John.stringify(result.unwrap(), indentation);
        throw new JsonException(result.message())
                .withSpan(result.span());
    }

    public static String stringify(JsonElement element) {
        return John.stringify(element, 0);
    }

    public static String stringify(JsonElement element, int indentation) {
        var builder = new JsonBuilder(indentation);
        element.stringify(builder);
        return builder.toString();
    }
}
