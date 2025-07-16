package com.manchickas.john.template.object.property.type;

import com.manchickas.john.ast.JsonElement;
import com.manchickas.john.position.SourceSpan;
import com.manchickas.john.template.object.property.PropertyAccessor;
import com.manchickas.john.template.object.property.PropertyTemplate;
import com.manchickas.john.util.Result;
import com.manchickas.john.template.Template;

public final class RequiredPropertyTemplate<Instance, T> extends PropertyTemplate<Instance, T> {

    public RequiredPropertyTemplate(String property,
                                    Template<T> template,
                                    PropertyAccessor<Instance, T> accessor) {
        super(property, template, accessor);
    }

    @Override
    public Result<T> missingResult(SourceSpan span) {
        return Result.error("Expected the object to include '%s' as a property."
                .formatted(this.property), span);
    }
}
