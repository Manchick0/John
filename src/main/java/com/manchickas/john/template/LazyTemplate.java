package com.manchickas.john.template;

import com.manchickas.john.ast.JsonElement;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public final class LazyTemplate<T> implements Template<T> {

    private final Supplier<Template<T>> supplier;
    @Nullable
    private volatile Template<T> cached;

    public LazyTemplate(Supplier<Template<T>> supplier) {
        this.supplier = supplier;
        this.cached = null;
    }

    @Override
    public Result<T> parse(JsonElement element) {
        return this.getOrCache()
                .parse(element);
    }

    @Override
    public Result<JsonElement> serialize(T value) {
        return this.getOrCache()
                .serialize(value);
    }

    @Override
    public String name(IntSet encountered) {
        return this.getOrCache()
                .name(encountered);
    }

    private Template<T> getOrCache() {
        if (this.cached == null) {
            synchronized (this) {
                if (this.cached == null)
                    this.cached = this.supplier.get();
            }
        }
        return this.cached;
    }

    @Override
    public int hashCode() {
        return this.getOrCache().hashCode();
    }
}
