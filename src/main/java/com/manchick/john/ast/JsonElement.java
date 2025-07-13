package com.manchick.john.ast;

import com.manchick.john.util.JsonBuilder;
import com.manchick.john.exception.JsonException;
import com.manchick.john.path.JsonPath;
import com.manchick.john.position.SourceSpan;
import com.manchick.john.template.Template;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;

public abstract class JsonElement {

    @Nullable
    protected final SourceSpan span;
    protected WeakReference<JsonElement> parent;

    public JsonElement() {
        this(null);
    }

    public JsonElement(@Nullable SourceSpan span) {
        this.parent = new WeakReference<>(null);
        this.span = span;
    }

    @NotNull
    public <T> T as(Template<T> template) throws JsonException {
        var result = template.wrapParseMismatch(this);
        if (result.isSuccess())
            return result.unwrap();
        throw new JsonException(result.message())
                .withSpan(result.span());
    }

    @NotNull
    public JsonElement get(String path) throws JsonException {
        return this.get(JsonPath.compile(path));
    }

    @NotNull
    public JsonElement get(JsonPath path) throws JsonException {
        return path.traverse(this);
    }

    @NotNull
    public <T> T get(String path, Template<T> template) throws JsonException {
        return this.get(JsonPath.compile(path), template);
    }

    @NotNull
    public <T> T get(JsonPath path, Template<T> template) throws JsonException {
        return this.get(path).as(template);
    }

    @NotNull
    public JsonElement property(String name) throws JsonException {
        throw new JsonException("Expected an object.")
                .withSpan(this.span);
    }

    @NotNull
    public JsonElement subscript(int index) throws JsonException {
        throw new JsonException("Expected an array.")
                .withSpan(this.span);
    }

    public int length() {
        return 1;
    }

    public abstract void stringify(JsonBuilder builder);

    @Nullable
    public JsonElement parent() {
        return this.parent.get();
    }

    public void assignParent(@NotNull JsonElement element) {
        if (this.parent != null)
            this.parent.clear();
        this.parent = new WeakReference<>(element);
    }

    @Nullable
    public SourceSpan span() {
        return this.span;
    }
}
