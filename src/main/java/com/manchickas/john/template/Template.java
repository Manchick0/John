package com.manchickas.john.template;

import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.template.union.UnionTemplate;
import com.manchickas.john.ast.primitive.JsonBoolean;
import com.manchickas.john.ast.primitive.JsonNull;
import com.manchickas.john.ast.primitive.JsonNumber;
import com.manchickas.john.ast.primitive.JsonString;
import com.manchickas.john.position.SourceSpan;
import com.manchickas.john.template.array.ArrayTemplate;
import com.manchickas.john.template.number.type.MaxTemplate;
import com.manchickas.john.template.number.type.MinTemplate;
import com.manchickas.john.template.number.NumericTemplate;
import com.manchickas.john.template.number.type.RangeTemplate;
import com.manchickas.john.template.object.DiscriminatedUnionTemplate;
import com.manchickas.john.template.object.constructor.BiConstructor;
import com.manchickas.john.template.object.constructor.TetraConstructor;
import com.manchickas.john.template.object.constructor.TriConstructor;
import com.manchickas.john.template.object.constructor.UniConstructor;
import com.manchickas.john.template.object.property.PropertyAccessor;
import com.manchickas.john.template.object.property.PropertyTemplate;
import com.manchickas.john.template.object.property.type.RequiredPropertyTemplate;
import com.manchickas.john.template.object.type.BiRecordTemplate;
import com.manchickas.john.template.object.type.TetraRecordTemplate;
import com.manchickas.john.template.object.type.TriRecordTemplate;
import com.manchickas.john.template.object.type.UniRecordTemplate;
import com.manchickas.john.template.string.type.LiteralTemplate;
import com.manchickas.john.template.string.type.PatternTemplate;
import com.manchickas.john.template.string.StringTemplate;
import com.manchickas.john.util.Result;
import org.intellij.lang.annotations.Language;

import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
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
 * @param <T> the type that the {@link #serialize(Object)} operation consumes, and the {@link #parse(JsonElement)} operation produces.
 */
public interface Template<T> {

    /**
     * Represents a {@link Template} that only matches JSON nulls.
     *
     * @since 1.0.0
     */
    NullTemplate NULL = new NullTemplate() {

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
    StringTemplate STRING = new StringTemplate() {

        @Override
        public Template<String> caseInsensitive() {
            return this;
        }

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
     * Represents a {@link Template} that consists of multiple strategies, defined by the provided templates.
     * <br><br>
     * The strategies are attempted fuzzily, but strictly in-order, short-circuiting on the first match.
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
     * @return a {@link NumericTemplate} representing the open range.
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
    static StringTemplate literal(String literal) {
        return new LiteralTemplate(literal);
    }

    /**
     * Represents a {@link Template} that matches all strings that themselves match the provided regular expression.
     * @param regex the regular expression to match against.
     * @return a {@link Template} representing that {@link Pattern}
     */
    static StringTemplate pattern(@Language("RegExp") String regex) {
        return Template.pattern(Pattern.compile(regex));
    }

    /**
     * Represents a {@link Template} that matches all strings that themselves match the provided precompiled {@link Pattern}.
     * @param pattern the {@link Pattern} to match against.
     * @return a {@link Template} representing that {@link Pattern}
     */
    static StringTemplate pattern(Pattern pattern) {
        return new PatternTemplate(pattern);
    }

    /**
     * Represents a {@link Template} that delegates its operations to the template returned by the {@code resolver} function,
     * based on a <b>discriminator</b>, represented by the {@code discriminator} property template.
     * <br><br>
     * The {@code resolver} must return a template whose type is a subtype of the {@code instance} type parameter.
     *
     * @param discriminator the template of the discriminator.
     * @param resolver the function that maps a discriminator value to the appropriate template.
     * @return a {@link Template} representing the discriminated union.
     * @param <Instance> the supertype of all possible templates.
     * @param <Disc> the type of the discriminator property.
     */
    static <Instance, Disc> Template<Instance> discriminatedUnion(PropertyTemplate<Instance, Disc> discriminator,
                                                                  Function<Disc, Template<? extends Instance>> resolver) {
        return new DiscriminatedUnionTemplate<>(discriminator, resolver);
    }

    static <Instance, A> Template<Instance> record(PropertyTemplate<Instance, A> first,
                                                   UniConstructor<A, Instance> constructor) {
        return new UniRecordTemplate<>(first, constructor);
    }

    static <Instance, A, B> Template<Instance> record(PropertyTemplate<Instance, A> first,
                                                      PropertyTemplate<Instance, B> second,
                                                      BiConstructor<A, B, Instance> constructor) {
        return new BiRecordTemplate<>(first, second, constructor);
    }

    static <Instance, A, B, C> Template<Instance> record(PropertyTemplate<Instance, A> first,
                                                         PropertyTemplate<Instance, B> second,
                                                         PropertyTemplate<Instance, C> third,
                                                         TriConstructor<A, B, C, Instance> constructor) {
        return new TriRecordTemplate<>(first, second, third, constructor);
    }

    static <Instance, A, B, C, D> Template<Instance> record(PropertyTemplate<Instance, A> first,
                                                            PropertyTemplate<Instance, B> second,
                                                            PropertyTemplate<Instance, C> third,
                                                            PropertyTemplate<Instance, D> fourth,
                                                            TetraConstructor<A, B, C, D, Instance> constructor) {
        return new TetraRecordTemplate<>(first, second, third, fourth, constructor);
    }

    /**
     * Represents a {@link Template} that behaves identically to the one returned by the provided {@code supplier}, but
     * defers the initialization to its first usage.
     * <br><br>
     * The underlying template gets initialized when either {@link #parse(JsonElement)} or {@link #serialize(Object)}
     * gets invoked, memoizing the returned template afterward.
     *
     * @param supplier the supplier of the underlying {@link Template}.
     * @return a {@link Template} that defers its initialization to the first usage.
     * @param <T> the type of the template.
     */
    static <T> Template<T> lazy(Supplier<Template<T>> supplier) {
        return new LazyTemplate<>(supplier);
    }

    /**
     * Represents a {@link Template} that <b>never</b> matches.
     * <br><br>
     * The {@code never} template may be used in places where a template is mandatory, yet it should never get
     * reached under normal conditions. Such as for the exhaustiveness of a switch statement, used in conjunction
     * with a {@link #discriminatedUnion(PropertyTemplate, Function)} template.
     *
     * @param <T> the type of the template.
     * @return a {@link Template} that always produces a mismatch.
     */
    static <T> Template<T> never() {
        return new Template<>() {

            @Override
            public Result<T> parse(JsonElement element) {
                return Result.mismatch();
            }

            @Override
            public Result<JsonElement> serialize(T value) {
                return Result.mismatch();
            }

            @Override
            public String name() {
                return "never";
            }
        };
    }

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

    /**
     * Composes a {@link Template} that resolves to an array, the element type of which corresponds to that of the current template.
     * <br><br>
     * In order for the template to match, <b>each</b> element must satisfy the current template.
     *
     * @param factory the factory needed to create the resulting array with.
     * @return a {@link Template} representing an array equivalent of the template.
     */
    default Template<T[]> array(IntFunction<T[]> factory) {
        return new ArrayTemplate<>(this, factory);
    }

    /**
     * Composes a {@link Template} that accesses the provided {@code name} property on a {@link com.manchickas.john.ast.JsonObject}
     * using the current template, and serializes the property by first accessing it with the provided {@link PropertyAccessor}, and then delegating
     * to the current template.
     *
     * @param name the name of the property.
     * @param accessor a {@link PropertyAccessor} that specifies how to access the property from an instance.
     * @return a {@link Template} representing the {@link PropertyTemplate}.
     * @param <Instance> the type on which the property can be accessed.
     */
    default <Instance> PropertyTemplate<Instance, T> property(String name, PropertyAccessor<Instance, T> accessor) {
        return new RequiredPropertyTemplate<>(name, this, accessor);
    }

    default <V> Template<V> map(Function<T, V> mapper,
                                Function<V, T> remapper) {
        return new Template<>() {

            @Override
            public Result<V> parse(JsonElement element) {
                return Template.this.parse(element)
                        .map(mapper);
            }

            @Override
            public Result<JsonElement> serialize(V value) {
                return Template.this.serialize(remapper.apply(value));
            }

            @Override
            public String name() {
                return Template.this.name();
            }
        };
    }

    default <V> Template<V> flatMap(Function<T, Result<V>> mapper, 
                                    Function<V, T> remapper) {
        return new Template<>() {

            @Override
            public Result<V> parse(JsonElement element) {
                return Template.this.parse(element)
                        .flatMap(mapper);
            }

            @Override
            public Result<JsonElement> serialize(V value) {
                return Template.this.serialize(remapper.apply(value));
            }

            @Override
            public String name() {
                return Template.this.name();
            }
        };
    }

    default Template<T> refine(Predicate<T> predicate, Supplier<String> message) {
        return new Template<>() {


            @Override
            public Result<T> parse(JsonElement element) {
                return Template.this.parse(element)
                        .flatMap(value -> {
                            if (predicate.test(value))
                                return Result.success(value);
                            return Result.error(
                                    message.get(),
                                    element.span()
                            );
                        });
            }

            @Override
            public Result<JsonElement> serialize(T value) {
                if (predicate.test(value))
                    return Template.this.serialize(value);
                return Result.error(
                        message.get(),
                        SourceSpan.lineWide(value.toString(), 1)
                );
            }

            @Override
            public String name() {
                return Template.this.name();
            }
        };
    }

    /**
     * Composes a {@link Template} that supplies a default {@code other} value if the underlying template couldn't produce a value itself.
     * <br><br>
     * The composed {@link Template} effectively never mismatches for {@link #parse(JsonElement)} operations.
     * @param other the default value to supply.
     * @return
     */
    default Template<T> orElse(T other) {
        return new Template<>() {

            @Override
            public Result<T> parse(JsonElement element) {
                var result = Template.this.parse(element);
                if (result.isSuccess())
                    return result;
                return Result.success(other);
            }

            @Override
            public Result<JsonElement> serialize(T value) {
                var result = Template.this.serialize(value);
                if (result.isSuccess())
                    return result;
                return Template.this.serialize(other);
            }

            @Override
            public String name() {
                return Template.this.name() + '?';
            }
        };
    }
}
