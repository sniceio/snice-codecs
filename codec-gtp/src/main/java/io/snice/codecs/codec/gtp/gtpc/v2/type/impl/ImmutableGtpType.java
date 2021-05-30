package io.snice.codecs.codec.gtp.gtpc.v2.type.impl;

import io.snice.buffer.Buffer;
import io.snice.codecs.codec.gtp.gtpc.v2.type.GtpType;

import java.util.Objects;

public abstract class ImmutableGtpType<T extends GtpType> implements GtpType {

    private final Buffer buffer;

    protected ImmutableGtpType(final Buffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public int size() {
        return buffer.capacity();
    }

    /**
     * Get the raw underlying byte-buffer that represents this {@link GtpType}.
     * Since a {@link Buffer} is immutable, it is safe to share between threads.
     *
     * @return
     */
    @Override
    public Buffer getBuffer() {
        return buffer;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ImmutableGtpType<?> that = (ImmutableGtpType<?>) o;
        return buffer.equals(that.buffer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(buffer);
    }
}
