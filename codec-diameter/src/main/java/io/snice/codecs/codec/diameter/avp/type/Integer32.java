package io.snice.codecs.codec.diameter.avp.type;

import io.snice.buffer.Buffer;
import io.snice.buffer.WritableBuffer;

import java.util.Objects;

public interface Integer32 extends DiameterType {

    static Integer32 parse(final Buffer data) {
        return new DefaultInteger32(data.getInt(0));
    }

    static Integer32 parse(final String data) {
        return new DefaultInteger32(Integer.parseInt(data));
    }

    static Integer32 of(final int value) {
        return new DefaultInteger32(value);
    }

    /**
     * Convert the given long value into an {@link Integer32} ONLY if no
     * information will be lost in the conversion. If you don't care
     * if you loose any information in the conversion from long to int,
     * you have to do this yourself and then call the {@link #of(int)} yourself.
     */
    static Integer32 of(final long value) {
        return new DefaultInteger32(Math.toIntExact(value));
    }

    int getValue();

    @Override
    default void writeValue(final WritableBuffer buffer) {
        buffer.write(getValue());
    }

    @Override
    default int size() {
        return 4;
    }

    class DefaultInteger32 implements Integer32 {
        private final int value;

        private DefaultInteger32(final int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final DefaultInteger32 that = (DefaultInteger32) o;
            return value == that.value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }
}
