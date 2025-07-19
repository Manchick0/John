package com.manchickas.john.template.object.type;

import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.ast.JsonObject;
import com.manchickas.john.template.object.RecordTemplate;
import com.manchickas.john.template.object.constructor.OctaConstructor;
import com.manchickas.john.template.object.property.PropertyTemplate;
import com.manchickas.john.template.Result;

import java.util.List;

public final class OctaRecordTemplate<A, B, C, D, E, F, H, I, Instance> extends RecordTemplate<Instance> {

    private final PropertyTemplate<Instance, A> first;
    private final PropertyTemplate<Instance, B> second;
    private final PropertyTemplate<Instance, C> third;
    private final PropertyTemplate<Instance, D> fourth;
    private final PropertyTemplate<Instance, E> fifth;
    private final PropertyTemplate<Instance, F> sixth;
    private final PropertyTemplate<Instance, H> seventh;
    private final PropertyTemplate<Instance, I> eighth;
    private final OctaConstructor<A, B, C, D, E, F, H, I, Instance> constructor;

    public OctaRecordTemplate(PropertyTemplate<Instance, A> first,
                              PropertyTemplate<Instance, B> second,
                              PropertyTemplate<Instance, C> third,
                              PropertyTemplate<Instance, D> fourth,
                              PropertyTemplate<Instance, E> fifth,
                              PropertyTemplate<Instance, F> sixth,
                              PropertyTemplate<Instance, H> seventh,
                              PropertyTemplate<Instance, I> eighth,
                              OctaConstructor<A, B, C, D, E, F, H, I, Instance> constructor) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
        this.fifth = fifth;
        this.sixth = sixth;
        this.seventh = seventh;
        this.eighth = eighth;
        this.constructor = constructor;
    }

    @Override
    public Result<Instance> parse(JsonElement element) {
        if (element instanceof JsonObject)
            return this.first.parseAndPromote(element).flatMap(first ->
                this.second.parseAndPromote(element).flatMap(second ->
                        this.third.parseAndPromote(element).flatMap(third ->
                                this.fourth.parseAndPromote(element).flatMap(fourth ->
                                        this.fifth.parseAndPromote(element).flatMap(fifth ->
                                                this.sixth.parseAndPromote(element).flatMap(sixth ->
                                                        this.seventh.parseAndPromote(element).flatMap(seventh ->
                                                                this.eighth.parseAndPromote(element).flatMap(eighth -> {
                                                                    var instance = this.constructor.construct(first, second, third, fourth, fifth, sixth, seventh, eighth);
                                                                    return Result.success(instance);
                                                                }))))))));
        return Result.mismatch();
    }

    @Override
    protected List<PropertyTemplate<Instance, ?>> properties() {
        return List.of(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth);
    }
}
