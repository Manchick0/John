package com.manchick.john.template.object.type;

import com.manchick.john.ast.JsonElement;
import com.manchick.john.ast.JsonObject;
import com.manchick.john.util.Result;
import com.manchick.john.template.object.RecordTemplate;
import com.manchick.john.template.object.constructor.DecaConstructor;
import com.manchick.john.template.object.property.PropertyTemplate;

import java.util.List;

public final class DecaRecordTemplate<A, B, C, D, E, F, G, H, I, J, T> extends RecordTemplate<T> {

    private final PropertyTemplate<T, A> first;
    private final PropertyTemplate<T, B> second;
    private final PropertyTemplate<T, C> third;
    private final PropertyTemplate<T, D> fourth;
    private final PropertyTemplate<T, E> fifth;
    private final PropertyTemplate<T, F> sixth;
    private final PropertyTemplate<T, G> seventh;
    private final PropertyTemplate<T, H> eighth;
    private final PropertyTemplate<T, I> ninth;
    private final PropertyTemplate<T, J> tenth;
    private final DecaConstructor<A, B, C, D, E, F, G, H, I, J, T> constructor;

    public DecaRecordTemplate(PropertyTemplate<T, A> first,
                              PropertyTemplate<T, B> second,
                              PropertyTemplate<T, C> third,
                              PropertyTemplate<T, D> fourth,
                              PropertyTemplate<T, E> fifth,
                              PropertyTemplate<T, F> sixth,
                              PropertyTemplate<T, G> seventh,
                              PropertyTemplate<T, H> eighth,
                              PropertyTemplate<T, I> ninth,
                              PropertyTemplate<T, J> tenth,
                              DecaConstructor<A, B, C, D, E, F, G, H, I, J, T> constructor) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
        this.fifth = fifth;
        this.sixth = sixth;
        this.seventh = seventh;
        this.eighth = eighth;
        this.ninth = ninth;
        this.tenth = tenth;
        this.constructor = constructor;
    }

    @Override
    public Result<T> parse(JsonElement element) {
        if (element instanceof JsonObject)
            return this.first.wrapParseMismatch(element).flatMap(first ->
                this.second.wrapParseMismatch(element).flatMap(second ->
                        this.third.wrapParseMismatch(element).flatMap(third ->
                                this.fourth.wrapParseMismatch(element).flatMap(fourth ->
                                        this.fifth.wrapParseMismatch(element).flatMap(fifth ->
                                                this.sixth.wrapParseMismatch(element).flatMap(sixth ->
                                                        this.seventh.wrapParseMismatch(element).flatMap(seventh ->
                                                                this.eighth.wrapParseMismatch(element).flatMap(eighth ->
                                                                        this.ninth.wrapParseMismatch(element).flatMap(ninth ->
                                                                                this.tenth.wrapParseMismatch(element).flatMap(tenth -> {
                                                                                    var instance = this.constructor.construct(first, second, third, fourth, fifth,
                                                                                            sixth, seventh, eighth, ninth, tenth);
                                                                                    return Result.success(instance);
                                                                                }))))))))));
        return Result.mismatch();
    }

    @Override
    protected List<PropertyTemplate<T, ?>> properties() {
        return List.of(this.first, this.second, this.third, this.fourth, this.fifth,
                this.sixth, this.seventh, this.eighth, this.ninth, this.tenth);
    }
}
