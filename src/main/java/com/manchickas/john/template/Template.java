package com.manchickas.john.template;

import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.template.object.MapTemplate;
import com.manchickas.john.template.object.constructor.*;
import com.manchickas.john.template.object.type.*;
import com.manchickas.john.template.union.UnionTemplate;
import com.manchickas.john.ast.primitive.JsonBoolean;
import com.manchickas.john.ast.primitive.JsonNumber;
import com.manchickas.john.ast.primitive.JsonString;
import com.manchickas.john.position.SourceSpan;
import com.manchickas.john.template.array.ArrayTemplate;
import com.manchickas.john.template.number.type.MaxTemplate;
import com.manchickas.john.template.number.type.MinTemplate;
import com.manchickas.john.template.number.NumericTemplate;
import com.manchickas.john.template.number.type.RangeTemplate;
import com.manchickas.john.template.object.DiscriminatedUnionTemplate;
import com.manchickas.john.template.object.property.PropertyAccessor;
import com.manchickas.john.template.object.property.PropertyTemplate;
import com.manchickas.john.template.object.property.type.RequiredPropertyTemplate;
import com.manchickas.john.template.string.LiteralTemplate;
import com.manchickas.john.template.string.PatternTemplate;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
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
    NullTemplate NULL = new NullTemplate();

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
        public String name(IntSet encountered) {
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
        public String name(IntSet encountered) {
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
        public String name(IntSet encountered) {
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
        public String name(IntSet encountered) {
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
     * @since 1.0.0
     */
    static NumericTemplate range(Number min, Number max) {
        return new RangeTemplate(min, max);
    }

    /**
     * Represents a {@link NumericTemplate} that matches all numbers lower than or equal to the provided bound.
     *
     * @param max the upper bound, inclusive.
     * @return a {@link NumericTemplate} representing the open range.
     * @since 1.0.0
     */
    static NumericTemplate max(Number max) {
        return new MaxTemplate(max);
    }

    /**
     * Represents a {@link NumericTemplate} that matches all numbers greater than or equal to the provided bound.
     *
     * @param min the lower bound, inclusive.
     * @return a {@link NumericTemplate} representing the open range.
     * @since 1.0.0
     */
    @Contract("_ -> new")
    static @NotNull NumericTemplate min(Number min) {
        return new MinTemplate(min);
    }

    /**
     * Represents a {@link Template} that only matches the provided literal string.
     *
     * @param literal the literal to match, case-sensitive.
     * @return a {@link Template} representing the literal.
     * @since 1.0.0
     */
    @Contract(value = "_ -> new", pure = true)
    static @NotNull LiteralTemplate literal(String literal) {
        return new LiteralTemplate(literal);
    }

    /**
     * Represents a {@link Template} that matches all strings that themselves match the provided regular expression.
     * @param regex the regular expression to match against.
     * @return a {@link Template} representing that {@link Pattern}
     * @since 1.0.0
     */
    @Contract("_ -> new")
    static @NotNull Template<String> pattern(@Language("RegExp") String regex) {
        return Template.pattern(Pattern.compile(regex));
    }

    /**
     * Represents a {@link Template} that matches all strings that themselves match the provided precompiled {@link Pattern}.
     * @param pattern the {@link Pattern} to match against.
     * @return a {@link Template} representing that {@link Pattern}
     * @since 1.0.0
     */
    @Contract(value = "_ -> new", pure = true)
    static @NotNull Template<String> pattern(Pattern pattern) {
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
     * @since 1.0.0
     */
    @Contract(value = "_, _ -> new", pure = true)
    static <Instance, Disc> @NotNull Template<Instance> discriminatedUnion(PropertyTemplate<Instance, Disc, ?> discriminator,
                                                                           Function<Disc, Template<? extends Instance>> resolver) {
        return new DiscriminatedUnionTemplate<>(discriminator, resolver);
    }

    /**
     * Represents a {@link Template} that matches any <b>arbitrary-keyed</b> JSON object, as long as each entry
     * of the object satisfies the provided {@code template}.
     *
     * @param template the template of an entry.
     * @return a {@link Template} representing the map.
     * @param <T> the type of the template.
     * @since 1.1.0
     */
    static <T> Template<Map<String, T>> map(Template<T> template) {
        return new MapTemplate<>(template);
    }

    @Contract("_, _ -> new")
    static <Instance, A> @NotNull Template<Instance> record(PropertyTemplate<Instance, A, ?> first,
                                                            UniConstructor<A, Instance> constructor) {
        return new UniRecordTemplate<>(first, constructor);
    }

    @Contract("_, _, _ -> new")
    static <Instance, A, B> @NotNull Template<Instance> record(PropertyTemplate<Instance, A, ?> first,
                                                               PropertyTemplate<Instance, B, ?> second,
                                                               BiConstructor<A, B, Instance> constructor) {
        return new BiRecordTemplate<>(first, second, constructor);
    }

    @Contract("_, _, _, _ -> new")
    static <Instance, A, B, C> @NotNull Template<Instance> record(PropertyTemplate<Instance, A, ?> first,
                                                                  PropertyTemplate<Instance, B, ?> second,
                                                                  PropertyTemplate<Instance, C, ?> third,
                                                                  TriConstructor<A, B, C, Instance> constructor) {
        return new TriRecordTemplate<>(first, second, third, constructor);
    }

    @Contract("_, _, _, _, _ -> new")
    static <Instance, A, B, C, D> @NotNull Template<Instance> record(PropertyTemplate<Instance, A, ?> first,
                                                                     PropertyTemplate<Instance, B, ?> second,
                                                                     PropertyTemplate<Instance, C, ?> third,
                                                                     PropertyTemplate<Instance, D, ?> fourth,
                                                                     TetraConstructor<A, B, C, D, Instance> constructor) {
        return new TetraRecordTemplate<>(first, second, third, fourth, constructor);
    }

    @Contract("_, _, _, _, _, _ -> new")
    static <Instance, A, B, C, D, E> @NotNull Template<Instance> record(PropertyTemplate<Instance, A, ?> first,
                                                                        PropertyTemplate<Instance, B, ?> second,
                                                                        PropertyTemplate<Instance, C, ?> third,
                                                                        PropertyTemplate<Instance, D, ?> fourth,
                                                                        PropertyTemplate<Instance, E, ?> fifth,
                                                                        PentaConstructor<A, B, C, D, E, Instance> constructor) {
        return new PentaRecordTemplate<>(first, second, third, fourth, fifth, constructor);
    }

    @Contract("_, _, _, _, _, _, _ -> new")
    static <Instance, A, B, C, D, E, F> @NotNull Template<Instance> record(PropertyTemplate<Instance, A, ?> first,
                                                                           PropertyTemplate<Instance, B, ?> second,
                                                                           PropertyTemplate<Instance, C, ?> third,
                                                                           PropertyTemplate<Instance, D, ?> fourth,
                                                                           PropertyTemplate<Instance, E, ?> fifth,
                                                                           PropertyTemplate<Instance, F, ?> sixth,
                                                                           HexaConstructor<A, B, C, D, E, F, Instance> constructor) {
        return new HexaRecordTemplate<>(first, second, third, fourth, fifth, sixth, constructor);
    }

    @Contract("_, _, _, _, _, _, _, _ -> new")
    static <Instance, A, B, C, D, E, F, G> @NotNull Template<Instance> record(PropertyTemplate<Instance, A, ?> first,
                                                                              PropertyTemplate<Instance, B, ?> second,
                                                                              PropertyTemplate<Instance, C, ?> third,
                                                                              PropertyTemplate<Instance, D, ?> fourth,
                                                                              PropertyTemplate<Instance, E, ?> fifth,
                                                                              PropertyTemplate<Instance, F, ?> sixth,
                                                                              PropertyTemplate<Instance, G, ?> seventh,
                                                                              HeptaConstructor<A, B, C, D, E, F, G, Instance> constructor) {
        return new HeptaRecordTemplate<>(first, second, third, fourth, fifth, sixth, seventh, constructor);
    }

    @Contract("_, _, _, _, _, _, _, _, _ -> new")
    static <Instance, A, B, C, D, E, F, G, H> @NotNull Template<Instance> record(PropertyTemplate<Instance, A, ?> first,
                                                                                 PropertyTemplate<Instance, B, ?> second,
                                                                                 PropertyTemplate<Instance, C, ?> third,
                                                                                 PropertyTemplate<Instance, D, ?> fourth,
                                                                                 PropertyTemplate<Instance, E, ?> fifth,
                                                                                 PropertyTemplate<Instance, F, ?> sixth,
                                                                                 PropertyTemplate<Instance, G, ?> seventh,
                                                                                 PropertyTemplate<Instance, H, ?> eighth,
                                                                                 OctaConstructor<A, B, C, D, E, F, G, H, Instance> constructor) {
        return new OctaRecordTemplate<>(first, second, third, fourth, fifth, sixth, seventh, eighth, constructor);
    }


    /**
     * Represents a {@link Template} that behaves identically to the one returned by the provided {@code supplier}, but
     * defers its initialization to the first usage.
     * <br><br>
     * The underlying template gets first initialized when either the {@link #parse(JsonElement)} the {@link #serialize(Object)},
     * the {@link #name(IntSet)} or the {@link Object#hashCode()} method gets invoked, <b>memoizing</b> the returned template afterward.
     *
     * @param supplier the supplier of the underlying {@link Template}.
     * @return a {@link Template} that defers its initialization to the first usage.
     * @param <T> the type of the template.
     * @since 1.0.0
     */
    @Contract(value = "_ -> new", pure = true)
    static <T> @NotNull Template<T> lazy(Supplier<Template<T>> supplier) {
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
     * @since 1.0.0
     */
    @Contract(value = " -> new", pure = true)
    static <T> @NotNull Template<T> never() {
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
            public String name(IntSet encountered) {
                return "never";
            }
        };
    }

    default Result<T> parseAndPromote(JsonElement element) {
        return this.parse(element).promoteMismatch("Expected a value that would satisfy the template of type '%s'"
                .formatted(this.name(new IntOpenHashSet())), element.span());
    }

    default Result<JsonElement> serializeAndPromote(T value) {
        return this.serialize(value).promoteMismatch("Expected a value that would satisfy the template of type '%s'"
                .formatted(this.name(new IntOpenHashSet())), SourceSpan.lineWide(value.toString(), 1));
    }

    /**
     * Attempts to parse the provided {@link JsonElement}.
     *
     * @param element the element to parse.
     * @return a {@link Result} representing the state of the operation.
     * @since 1.0.0
     */
    Result<T> parse(JsonElement element);

    /**
     * Attempts to serialize the provided {@code value} back into a {@link JsonElement}.
     * @param value the value to serialize.
     * @return a {@link Result} representing the state of the operation.
     * @since 1.0.0
     */
    Result<JsonElement> serialize(@Nullable T value);

    /**
     * Builds a descriptive name for the template.
     *
     * @return the string representation of the template.
     * @since 1.0.0
     */
    String name(IntSet encountered);

    /**
     * Composes a {@link Template} that yields an array using the current template for every element in the array.
     * <br><br>
     * In order for the composed template to match, <b>each</b> element must satisfy the current template.
     *
     * @param factory the factory needed to create the resulting array with.
     * @return a {@link Template} representing an array equivalent of the template.
     * @since 1.0.0
     */
    @Contract(value = "_ -> new", pure = true)
    default @NotNull Template<T[]> array(IntFunction<T[]> factory) {
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
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    default <Instance, Self extends PropertyTemplate<Instance, T, Self>> Self property(String name, PropertyAccessor<Instance, T> accessor) {
        return (Self) new RequiredPropertyTemplate<>(
                name,
                this,
                accessor,
                __ -> false
        );
    }

    /**
     * Composes a {@link Template} that, if the current template could yield a value, transforms it using the provided {@code mapper} function.
     * <br><br>
     * During serialization, the composed {@link Template} transforms the value back to the one requested by the underlying template
     * using the {@code remapper} function, and delegates the process back to it.
     *
     * @param mapper the function to use for forward transformation.
     * @param remapper the function to use for backward transformation.
     * @return a {@link Template} that transforms the current template's value.
     * @param <V> the type of the composed template.
     * @see #flatMap(Function, Function)
     * @since 1.0.0
     */
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
            public String name(IntSet encountered) {
                return Template.this.name(encountered);
            }

            @Override
            public int hashCode() {
                return Template.this.hashCode();
            }
        };
    }

    /**
     * Composes a {@link Template} that, if the current template could yield a value, transforms it to a {@link Result} using the provided {@code mapper} function,
     * which then represents the state of the operation.
     * <br><br>
     * During serialization, the composed {@link Template} transforms the value back to the one requested by the underlying template
     * using the {@code remapper} function, and delegates the process back to it.
     *
     * @param mapper the function to use for forward transformation.
     * @param remapper the function to use for backward transformation.
     * @return a {@link Template} that transforms the current template's value.
     * @param <V> the type of the composed template.
     * @see #map(Function, Function)
     * @since 1.0.0
     */
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
            public String name(IntSet encountered) {
                return Template.this.name(encountered);
            }

            @Override
            public int hashCode() {
                return Template.this.hashCode();
            }
        };
    }

    /**
     * Composes a {@link Template} that applies an additional validation step on top of the current template.
     * <br><br>
     * In order for the composed template to match, the underlying one has to match first, and the yielded value
     * should then satisfy the provided {@code predicate}. If it doesn't, an error is raised with the result of the
     * provided {@code message} supplier.
     *
     * @param predicate the predicate to validate the value against.
     * @param message the supplier of the error message.
     * @return a {@link Template} that yields the current template's value, if it satisfies the given predicate; otherwise, errors with the supplied error message.
     * @since 1.0.0
     */
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
            public String name(IntSet encountered) {
                return Template.this.name(encountered);
            }

            @Override
            public int hashCode() {
                return Template.this.hashCode();
            }
        };
    }

    /**
     * Composes a {@link Template} that, if the current template doesn't yield a value itself,
     * matches on JSON {@code null}s.
     *
     * @return a {@link Template} that yield the current template's value, or {@code null}.
     */
    default Template<T> optional() {
        return this.optional(() -> null);
    }

    /**
     * Composes a {@link Template} that, if the current template doesn't yield a value itself,
     * matches on JSON {@code null}s with the value provided by the given {@link Supplier}.
     *
     * @return a {@link Template} that yield the current template's value, or {@code null}.
     */
    default Template<T> optional(@NotNull Supplier<@Nullable T> supplier) {
        return new OptionalTemplate<>(this, supplier);
    }
}
