package com.manchickas.john.reader;

import com.manchickas.john.position.SourceSpan;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;

public class StringReader {

    private final String src;
    private final ObjectArrayFIFOQueue<Position> stamps;
    private final IntArrayFIFOQueue lineBounds;
    private final Int2ObjectMap<String> readLines;
    private int cursor;
    private int column;
    private int line;

    public StringReader(String src) {
        this.src = src;
        this.stamps = new ObjectArrayFIFOQueue<>();
        this.lineBounds = new IntArrayFIFOQueue();
        this.readLines = new Int2ObjectOpenHashMap<>();
        this.cursor = 0;
        this.column = 1;
        this.line = 1;
    }

    public static boolean isSign(int c) {
        return c == '+' || c == '-';
    }

    public static boolean isDigit(int c) {
        return c >= '0' && c <= '9';
    }

    public static boolean isHexDigit(int c) {
        return StringReader.isDigit(c) || c >= 'a' && c <= 'f' || c >= 'A' && c <= 'F';
    }

    public static boolean isWhitespace(int c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\r';
    }

    public int peek() {
        return this.src.codePointAt(this.cursor);
    }

    public int peekAhead(int amount) {
        var cursor = this.cursor;
        while (amount-- > 0) {
            var c = this.src.codePointAt(cursor);
            cursor += Character.charCount(c);
        }
        return this.src.codePointAt(cursor);
    }

    public int read(int amount) {
        while (--amount > 0)
            this.read();
        return this.read();
    }

    public int read() {
        var c = this.peek();
        this.cursor += Character.charCount(c);
        if (c == '\n') {
            this.lineBounds.enqueueFirst(this.cursor);
            this.column = 1;
            this.line++;
            return '\n';
        }
        this.column += 1;
        return c;
    }

    public boolean canRead() {
        return this.cursor < this.src.length();
    }

    public boolean canRead(int amount) {
        var cursor = this.cursor;
        var i = 0;
        while (cursor < this.src.length()) {
            var c = this.src.codePointAt(cursor);
            cursor += Character.charCount(c);
            if (++i == amount)
                break;
        }
        return i == amount;
    }

    protected void pushStamp() {
        this.stamps.enqueueFirst(new Position(this.cursor, this.column, this.line));
    }

    protected Position popStamp() {
        return this.stamps.dequeue();
    }

    protected Position peekStamp() {
        return this.stamps.first();
    }

    public void backtrack() {
        this.backtrack(this.popStamp());
    }

    public void backtrack(Position position) {
        this.cursor = position.cursor();
        this.column = position.column();
        this.line = position.line();
    }

    public SourceSpan span() {
        return this.span(this.popStamp());
    }

    public SourceSpan span(Position start) {
        return new SourceSpan(this.readLine(), this.line,
                start.column(), this.column - 1);
    }

    public SourceSpan charSpan() {
        return SourceSpan.charWide(this.readLine(), this.line, this.column);
    }

    public SourceSpan relativeSpan(int left, int right) {
        return new SourceSpan(this.readLine(), this.line,
                this.column - left, this.column + right - 1);
    }

    public String slice() {
        var stamp = this.popStamp();
        var start = stamp.cursor();
        return this.src.substring(start, this.cursor);
    }

    public String readLine() {
        var cached = this.readLines.get(this.line);
        if (cached == null) {
            this.pushStamp();
            this.cursor = this.lineBounds.isEmpty() ? 0
                    : this.lineBounds.firstInt();
            var builder = new StringBuilder();
            while (this.canRead()) {
                var c = this.peek();
                if (c == '\n')
                    break;
                builder.appendCodePoint(this.read());
            }
            var line = builder.toString();
            this.readLines.put(this.line, line);
            this.backtrack();
            return line;
        }
        return cached;
    }

    public record Position(int cursor, int column, int line) {}
}
