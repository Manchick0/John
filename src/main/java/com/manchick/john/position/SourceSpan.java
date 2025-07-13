package com.manchick.john.position;

import org.jetbrains.annotations.NotNull;

public record SourceSpan(String sourceLine, int line, int start, int end) {

    private static final String LIGHT_GRAY = "\u001B[37m";
    private static final String GRAY = "\u001B[90m";
    private static final String RED = "\u001B[91m";
    private static final String RESET = "\u001B[0m";

    public static SourceSpan lineWide(String sourceLine, int line) {
        return new SourceSpan(sourceLine, line, 0, sourceLine.length() - 1);
    }

    public static SourceSpan charWide(String sourceLine, int line, int column) {
        return new SourceSpan(sourceLine, line, column, column);
    }

    public String underlineSource(boolean format) {
        return new StringBuilder()
                .append(format ? LIGHT_GRAY : "")
                .append(this.sourceLine, 0, this.start - 1)
                .append(format ? RESET : "")
                .append(format ? RED : "")
                .append(this.sourceLine, this.start - 1, this.end)
                .append(format ? RESET : "")
                .append(format ? LIGHT_GRAY : "")
                .append(this.sourceLine, this.end, this.sourceLine.length())
                .append(format ? RESET : "")
                .append('\n')
                .repeat(" ", this.start - 1)
                .append(format ? GRAY : "")
                .repeat("^", this.end - this.start + 1)
                .append(format ? RESET : "")
                .toString();
    }

    public SourceSpan extend(SourceSpan span) {
        if (span.line != this.line)
            return this;
        return new SourceSpan(this.sourceLine, this.line,
                Math.min(this.start, span.start),
                Math.max(this.end, span.end));
    }

    @Override
    public @NotNull String toString() {
        return "(" + this.line + ":" + this.start + "-" + this.end + ")";
    }
}
