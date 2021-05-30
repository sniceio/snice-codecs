package io.snice.codecs.codec.diameter.avp.type;

import io.snice.buffer.Buffer;
import io.snice.buffer.WritableBuffer;

import java.util.Objects;

public interface Unsigned32 extends DiameterType {

    /**
     * Will assume the buffer should be treated as a raw byte-array and will
     * try and create a unsigned int (long) from the first 4 bytes of the
     * buffer.
     *
     * Note: if you actually want this one to be interpreted as a string
     * instead, then you first convert the buffer to a string and then call {@link #parse(String)}
     * instead.
     *
     */
    static Unsigned32 parse(final Buffer data) {
        return new DefaultUnsigned32(data.getUnsignedInt(0));
    }

    /**
     * Attempt to parse the given string into a {@link Unsigned32}.
     *
     * @param data the value to parse
     * @return
     * @throws NumberFormatException in case the given data cannot be parsed into a {@link Long}
     */
    static Unsigned32 parse(final String data) throws NumberFormatException {
        return new DefaultUnsigned32(Long.parseLong(data));
    }

    static Unsigned32 of(final long value) {
        return new DefaultUnsigned32(value);
    }

    long getValue();

    @Override
    default int size() {
        return 4;
    }

    @Override
    default void writeValue(final WritableBuffer buffer) {
        buffer.write((int) getValue());
    }

    class DefaultUnsigned32 implements Unsigned32 {
        private final long value;

        private DefaultUnsigned32(final long value) {
            this.value = value;
        }

        @Override
        public long getValue() {
            return value;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final DefaultUnsigned32 that = (DefaultUnsigned32) o;
            return value == that.value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }
}
