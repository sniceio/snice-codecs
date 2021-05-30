package io.snice.codecs.codec.gtp.impl;

import io.snice.buffer.Buffer;
import io.snice.buffer.WritableBuffer;
import io.snice.codecs.codec.gtp.Teid;

import static io.snice.buffer.Buffers.assertBufferCapacity;

public class TeidImpl implements Teid {

    public static Teid of(final Buffer buffer) {
        assertBufferCapacity(buffer, 4, "The length of the TEID must be 4 bytes");
        return new TeidImpl(buffer);
    }

    @Override
    public void write(final WritableBuffer buffer) {
        buffer.write(this.buffer.getByte(0));
        buffer.write(this.buffer.getByte(1));
        buffer.write(this.buffer.getByte(2));
        buffer.write(this.buffer.getByte(3));
    }

    @Override
    public void setAtIndex(final int index, final WritableBuffer buffer) {
        buffer.setByte(index, this.buffer.getByte(0));
        buffer.setByte(index + 1, this.buffer.getByte(1));
        buffer.setByte(index + 2, this.buffer.getByte(2));
        buffer.setByte(index + 3, this.buffer.getByte(3));
    }

    private final Buffer buffer;

    private TeidImpl(final Buffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public Buffer getBuffer() {
        return buffer;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final TeidImpl teid = (TeidImpl) o;
        return buffer.equals(teid.buffer);
    }

    @Override
    public String toString() {
        return buffer.toHexString();
    }

    @Override
    public int hashCode() {
        return buffer.hashCode();
    }
}

