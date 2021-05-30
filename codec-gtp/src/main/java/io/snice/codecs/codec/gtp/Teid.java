package io.snice.codecs.codec.gtp;

import io.snice.buffer.Buffer;
import io.snice.buffer.Buffers;
import io.snice.buffer.WritableBuffer;
import io.snice.codecs.codec.gtp.impl.TeidImpl;

/**
 * Tunnel Endpoint identifier is used to multiplex different connections across
 * the same GTP tunnel.
 */
public interface Teid {

    Teid ZEROS = Teid.of(Buffers.wrap((byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00));

    /**
     * Create a random {@link Teid}
     */
    static Teid random() {
        return of(Buffers.random(4));
    }

    static Teid of(final Buffer buffer) {
        return TeidImpl.of(buffer);
    }

    static Teid of(final byte a, final byte b, final byte c, final byte d) {
        return TeidImpl.of(Buffer.of(a, b, c, d));
    }

    static Teid of(final int a, final int b, final int c, final int d) {
        return TeidImpl.of(Buffer.of((byte)a, (byte)b, (byte)c, (byte)d));
    }

    /**
     * Write the content (always 4 bytes) of this {@link Teid} into the given
     * {@link WritableBuffer}.
     *
     * @param buffer
     */
    void write(WritableBuffer buffer);

    /**
     * Set this {@link Teid} at the given start index.
     *
     * The difference between this method and the {@link #write(WritableBuffer)} one is that
     * this method will just set the 4 bytes of this {@link Teid} at the given index.
     *
     * This method will NOT advance the writer index of the {@link WritableBuffer} since it
     * is technically not writing, just setting bytes.
     *
     * @param startIndex the index of the first byte of this {@link Teid}.
     * @param buffer the buffer we'll be changing.
     */
    void setAtIndex(int startIndex, WritableBuffer buffer);

    Buffer getBuffer();
}
