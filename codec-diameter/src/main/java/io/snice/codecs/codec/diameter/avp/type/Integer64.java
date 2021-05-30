package io.snice.codecs.codec.diameter.avp.type;

import io.snice.buffer.Buffer;
import io.snice.buffer.WritableBuffer;

import java.util.Objects;

public interface Integer64 extends DiameterType {

    static Integer64 parse(final Buffer data) {
        return new DefaultInteger64(data.getLong(0));
    }

    /**
     * Attempt to parse the given string into a {@link Integer64}.
     *
     * @param data the value to parse
     * @return
     * @throws NumberFormatException in case the given data cannot be parsed into a {@link Long}
     */
    static Integer64 parse(final String data) throws NumberFormatException {
        return new DefaultInteger64(Long.parseLong(data));
    }

    static Integer64 of(final long value) {
        return new DefaultInteger64(value);
    }

    static Integer64 of(final int value) {
        return new DefaultInteger64(value);
    }

    long getValue();

    @Override
    default int size() {
        return 8;
    }

    @Override
    default void writeValue(final WritableBuffer buffer) {
        buffer.write(getValue());
    }

    class DefaultInteger64 implements Integer64 {
        private final long value;

        private DefaultInteger64(final long value) {
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
            final DefaultInteger64 that = (DefaultInteger64) o;
            return value == that.value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }
}
