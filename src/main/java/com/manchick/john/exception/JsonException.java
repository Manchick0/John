package com.manchick.john.exception;

import com.manchick.john.position.SourceSpan;
import org.jspecify.annotations.Nullable;

public class JsonException extends Exception {

    public JsonException(String message) {
        super(message);
    }

    public JsonException(String message, Object... args) {
        super(message.formatted(args));
    }

    public JsonException withSpan(@Nullable SourceSpan position) {
        return position != null
                ? new PositionedJsonException(this.getMessage(), position)
                : this;
    }

    public String getMessage(boolean format) {
        return this.getMessage();
    }
}
