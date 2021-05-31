package io.snice.codecs.codec.gtp.type;

import io.snice.buffer.Buffer;
import io.snice.buffer.WritableBuffer;

/**
 * Base type
 */
public interface GtpType {

    /**
     * The size of this {@link GtpType} in bytes. I.e., how many bytes
     * does it take to represent this value when externalized. Typically
     * used when the value is about to get written to e.g. the network.
     */
    int size();

    /**
     * Get the {@link Buffer} that represents this type. This is what you would write to
     * the underlying network.
     *
     * @return
     */
    Buffer getBuffer();

    default void writeValue(final WritableBuffer buffer) {
        throw new RuntimeException("Not implemented for " + getClass().getName() + " just yet");
    }
}
