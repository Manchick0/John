package com.manchickas.john.template;

import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.ast.primitive.JsonNull;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public final class OptionalTemplate<T> implements Template<T> {

    private final Template<T> template;
    private final @NotNull Supplier<@Nullable T> supplier;

    public OptionalTemplate(Template<T> template,
                            @NotNull Supplier<@Nullable T> supplier) {
        this.template = template;
        this.supplier = supplier;
    }

    @Override
    public Result<T> parse(JsonElement element) {
        var result = this.template.parse(element);
        if (result.isSuccess())
            return result;
        if (element instanceof JsonNull || element == null)
            return Result.success(this.supplier.get());
        return Result.mismatch();
    }

    @Override
    public Result<JsonElement> serialize(@Nullable T value) {
        var result = this.template.serialize(value);
        if (result.isSuccess())
            return result;
        if (value == null)
            return Result.success(new JsonNull());
        return Result.mismatch();
    }

    @Override
    public Template<T> optional(@NotNull Supplier<@Nullable T> supplier) {
        return new OptionalTemplate<>(this.template, supplier);
    }

    @Override
    public String name(IntSet encountered) {
        return this.template.name(encountered) + '?';
    }

    @Override
    public int hashCode() {
        return this.template.hashCode();
    }
}
