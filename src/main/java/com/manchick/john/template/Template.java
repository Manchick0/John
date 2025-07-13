package com.manchick.john.template;

import com.manchick.john.ast.JsonElement;
import com.manchick.john.ast.primitive.JsonBoolean;
import com.manchick.john.ast.primitive.JsonNull;
import com.manchick.john.ast.primitive.JsonNumber;
import com.manchick.john.ast.primitive.JsonString;
import com.manchick.john.position.SourceSpan;
import com.manchick.john.template.array.ArrayTemplate;
import com.manchick.john.template.number.MaxTemplate;
import com.manchick.john.template.number.MinTemplate;
import com.manchick.john.template.number.NumericTemplate;
import com.manchick.john.template.number.RangeTemplate;
import com.manchick.john.template.object.DiscriminatedUnion;
import com.manchick.john.template.object.constructor.BiConstructor;
import com.manchick.john.template.object.constructor.TetraConstructor;
import com.manchick.john.template.object.constructor.TriConstructor;
import com.manchick.john.template.object.constructor.UniConstructor;
import com.manchick.john.template.object.property.PropertyAccessor;
import com.manchick.john.template.object.property.PropertyTemplate;
import com.manchick.john.template.object.type.BiRecordTemplate;
import com.manchick.john.template.object.type.TetraRecordTemplate;
import com.manchick.john.template.object.type.TriRecordTemplate;
import com.manchick.john.template.object.type.UniRecordTemplate;
import com.manchick.john.template.string.LiteralTemplate;
import com.manchick.john.template.string.PatternTemplate;
import com.manchick.john.util.Result;
import org.intellij.lang.annotations.Language;

import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.regex.Pattern;

/**
 * Represents a <b>JSON parsing strategy</b>.
 * <br><br>
 * Templates are used excessively and are tightly connected to other library components.
 * Because of how core they are to John, most JSON-related operations require templates in one
 * way or another.
 * <br><br>
 * A single {@link Template} instance handles <b>serialization</b>, <b>deserialization</b>, and <b>validation</b>
 * all at once, and may be reused throughout your whole application. You should thus define most templates as {@code static final} constants.
 *
 * @param <T> the type of the template.
 */
public interface Template<T> {

    /**
     * Represents a {@link Template} that only matches JSON nulls.
     *
     * @since 1.0.0
     */
    Template<Void> NULL = new Template<>() {

        @Override
        public Result<Void> parse(JsonElement element) {
            if (element instanceof JsonNull)
                return Result.success(null);
            return Result.mismatch();
        }

        @Override
        public Result<JsonElement> serialize(Void value) {
            return Result.success(null);
        }

        @Override
        public String name() {
            return "null";
        }
    };

    /**
     * Represents a {@link Template} that matches all JSON elements.
     *
     * @since 1.0.0
     */
    Template<JsonElement> ANY = new Template<>() {

        @Override
        public Result<JsonElement> parse(JsonElement element) {
            return Result.success(element);
        }

        @Override
        public Result<JsonElement> serialize(JsonElement value) {
            return Result.success(value);
        }

        @Override
        public String name() {
            return "any";
        }
    };

    /**
     * Represents a {@link Template} that only matches JSON strings.
     *
     * @since 1.0.0
     */
    Template<String> STRING = new Template<>() {

        @Override
        public Result<String> parse(JsonElement element) {
            if (element instanceof JsonString string)
                return Result.success(string.value());
            return Result.mismatch();
        }

        @Override
        public Result<JsonElement> serialize(String value) {
            return Result.success(new JsonString(value));
        }

        @Override
        public String name() {
            return "string";
        }
    };

    /**
     * Represents a {@link Template} that only matches JSON numbers.
     *
     * @since 1.0.0
     */
    NumericTemplate NUMBER = new NumericTemplate() {

        @Override
        public Result<Number> parse(JsonElement element) {
            if (element instanceof JsonNumber number)
                return Result.success(number.value());
            return Result.mismatch();
        }

        @Override
        public Result<JsonElement> serialize(Number value) {
            return Result.success(new JsonNumber(value));
        }

        @Override
        public String name() {
            return "number";
        }
    };

    /**
     * Represents a {@link Template} that only matches JSON booleans.
     *
     * @since 1.0.0
     */
    Template<Boolean> BOOLEAN = new Template<>() {

        @Override
        public Result<Boolean> parse(JsonElement element) {
            if (element instanceof JsonBoolean bool)
                return Result.success(bool.value());
            return Result.mismatch();
        }

        @Override
        public Result<JsonElement> serialize(Boolean value) {
            return Result.success(new JsonBoolean(value));
        }

        @Override
        public String name() {
            return "boolean";
        }
    };

    /**
     * Represents a {@link Template} that fuzzily attempts multiple strategies, defined by the provided templates.
     * <br><br>
     * The strategies are attempted strictly in-order, short-circuiting on the first match.
     *
     * @param templates the strategies to attempt.
     * @return a {@link Template} representing a union of the provided strategies.
     * @param <T> the type of all strategies.
     *
     * @since 1.0.0
     */
    @SafeVarargs
    static <T> Template<T> union(Template<T>... templates) {
        return new UnionTemplate<>(templates);
    }

    static <T> Template<T[]> array(Template<T> template, IntFunction<T[]> factory) {
        return new ArrayTemplate<>(template, factory);
    }

    /**
     * Represents a {@link NumericTemplate} that matches all numbers within the provided range.
     *
     * @param min the lower bound, inclusive.
     * @param max the upper bound, inclusive.
     * @return a {@link NumericTemplate} representing the specified range
     */
    static NumericTemplate range(Number min, Number max) {
        return new RangeTemplate(min, max);
    }

    /**
     * Represents a {@link NumericTemplate} that matches all numbers lower than or equal to the provided bound.
     *
     * @param max the upper bound, inclusive.
     * @return a {@link NumericTemplate} representing the open range.
     */
    static NumericTemplate max(Number max) {
        return new MaxTemplate(max);
    }

    /**
     * Represents a {@link NumericTemplate} that matches all numbers greater than or equal to the provided bound.
     *
     * @param min the lower bound, inclusive.
     * @return a {@link NumericTemplate} representing open range.
     */
    static NumericTemplate min(Number min) {
        return new MinTemplate(min);
    }

    /**
     * Represents a {@link Template} that only matches the provided literal string.
     *
     * @param literal the literal to match, case-sensitive.
     * @return a {@link Template} representing the literal.
     */
    static Template<String> literal(String literal) {
        return new LiteralTemplate(literal);
    }

    static Template<String> pattern(@Language("RegExp") String pattern) {
        return Template.pattern(Pattern.compile(pattern));
    }

    static Template<String> pattern(Pattern pattern) {
        return new PatternTemplate(pattern);
    }

    static <D, T> Template<T> discriminatedUnion(PropertyTemplate<T, D> discriminator,
                                                Function<D, Template<? extends T>> resolver) {
        return new DiscriminatedUnion<>(discriminator, resolver);
    }

    static <A, T> Template<T> record(PropertyTemplate<T, A> first, UniConstructor<A, T> constructor) {
        return new UniRecordTemplate<>(first, constructor);
    }

    static <A, B, T> Template<T> record(PropertyTemplate<T, A> first, PropertyTemplate<T, B> second, BiConstructor<A, B, T> constructor) {
        return new BiRecordTemplate<>(first, second, constructor);
    }

    static <A, B, C, T> Template<T> record(PropertyTemplate<T, A> first, PropertyTemplate<T, B> second, PropertyTemplate<T, C> third, TriConstructor<A, B, C, T> constructor) {
        return new TriRecordTemplate<>(first, second, third, constructor);
    }

    static <A, B, C, D, T> Template<T> record(PropertyTemplate<T, A> first, PropertyTemplate<T, B> second, PropertyTemplate<T, C> third, PropertyTemplate<T, D> fourth, TetraConstructor<A, B, C, D, T> constructor) {
        return new TetraRecordTemplate<>(first, second, third, fourth, constructor);
    }

    /**
     * Attempts to parse the provided {@link JsonElement} with the {@link #parse(JsonElement)} method,
     * then replaces any {@link Result.Mismatch} with {@link Result.Error}.
     *
     * @param element the element to parse.
     * @return a {@link Result}, representing the state of the operation.
     */
    default Result<T> wrapParseMismatch(JsonElement element) {
        var result = this.parse(element);
        if (result.isMismatch())
            return new Result.Error<>("Expected a value that would satisfy the template of type '%s'"
                    .formatted(this.name()), element.span());
        return result;
    }

    default Result<JsonElement> wrapSerializeMismatch(T value) {
        var result = this.serialize(value);
        if (result.isMismatch())
            return new Result.Error<>("Expected a value that would satisfy the template of type '%s'"
                    .formatted(this.name()), SourceSpan.lineWide(value.toString(), 1));
        return result;
    }

    /**
     * Attempts to parse the provided {@link JsonElement} against the template.
     *
     * @param element the element to parse.
     * @return a {@link Result} representing the operation state.
     */
    Result<T> parse(JsonElement element);
    Result<JsonElement> serialize(T value);
    String name();

    default <V> PropertyTemplate<V, T> property(String name, PropertyAccessor<V, T> accessor) {
        return new PropertyTemplate<>(name, this, accessor);
    }

    default <V> Template<V> map(Function<T, V> mapper, Function<V, T> remapper) {
        return new Template<>() {

            @Override
            public Result<V> parse(JsonElement element) {
                return Template.this.wrapParseMismatch(element).map(mapper);
            }

            @Override
            public Result<JsonElement> serialize(V value) {
                return Template.this.wrapSerializeMismatch(remapper.apply(value));
            }

            @Override
            public String name() {
                return Template.this.name();
            }
        };
    }

    default <V> Template<V> flatMap(Function<T, Result<V>> mapper, Function<V, T> remapper) {
        return new Template<>() {

            @Override
            public Result<V> parse(JsonElement element) {
                return Template.this.wrapParseMismatch(element).flatMap(mapper);
            }

            @Override
            public Result<JsonElement> serialize(V value) {
                return Template.this.wrapSerializeMismatch(remapper.apply(value));
            }

            @Override
            public String name() {
                return Template.this.name();
            }
        };
    }
}
