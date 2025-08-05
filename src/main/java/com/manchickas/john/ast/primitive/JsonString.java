package com.manchickas.john.ast.primitive;

import com.manchickas.john.position.SourceSpan;
import it.unimi.dsi.fastutil.chars.Char2CharMap;

public final class JsonString extends JsonPrimitive<String> {

    private static final Char2CharMap ESCAPES = Char2CharMap.ofEntries(
            Char2CharMap.entry('"', '"'),
            Char2CharMap.entry('/', '/'),
            Char2CharMap.entry('\\', '\\'),
            Char2CharMap.entry('\b', 'b'),
            Char2CharMap.entry('\f', 'f'),
            Char2CharMap.entry('\n', 'n'),
            Char2CharMap.entry('\r', 'r'),
            Char2CharMap.entry('\t', 't')
    );

    private final String value;

    public JsonString(String value) {
        this(null, value);
    }

    public JsonString(SourceSpan span, String value) {
        super(span);
        this.value = value;
    }

    @Override
    public String stringifyPattern() {
        return '"' + this.sanitize() + '"';
    }

    public String sanitize() {
        var builder = new StringBuilder();
        for (var i = 0; i < this.value.length(); i++) {
            var c = this.value.charAt(i);
            if (c == ' ') {
                builder.append("\\s!");
                continue;
            }
            if (ESCAPES.containsKey(c)) {
                var escape = ESCAPES.get(c);
                builder.append('\\')
                        .append('\\')
                        .append(escape);
                continue;
            }
            builder.append(c);
        }
        return builder.toString();
    }

    @Override
    public String value() {
        return this.value;
    }
}
