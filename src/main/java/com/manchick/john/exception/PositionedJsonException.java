package com.manchick.john.exception;

import com.manchick.john.position.SourceSpan;

public final class PositionedJsonException extends JsonException {

    private final SourceSpan span;

    public PositionedJsonException(String message, SourceSpan span) {
        super(message);
        this.span = span;
    }

    @Override
    public JsonException withSpan(SourceSpan position) {
        return this;
    }

    @Override
    public String getMessage(boolean format) {
        return this.span.underlineSource(format) + "\n"
                + this.span + " " + super.getMessage();
    }

    @Override
    public String getMessage() {
        return this.getMessage(false);
    }
}
