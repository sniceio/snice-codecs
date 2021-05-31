package io.snice.codecs.codec.gtp.type;

import io.snice.buffer.Buffer;
import io.snice.buffer.Buffers;
import io.snice.buffer.WritableBuffer;
import io.snice.codecs.codec.gtp.type.impl.ImmutableGtpType;

import static io.snice.preconditions.PreConditions.assertArgument;

public interface EbiType extends GtpType {

    static EbiType parse(final Buffer buffer) {
        assertArgument(buffer != null && buffer.capacity() == 1, "The EBI Type is only one byte long");
        return new DefaultEbiType(buffer);
    }

    static EbiType parse(final String buffer) {
        return parse(Buffers.wrap(buffer));
    }

    static EbiType ofValue(final Buffer buffer) {
        return parse(buffer);
    }

    static EbiType ofValue(final String buffer) {
        return parse(buffer);
    }

    static EbiType ofValue(final int value) {
        assertArgument(value >= 0 && value < 16, "The value can only be between 0-15 (inclusive)");
        final var writable = WritableBuffer.of(1);
        writable.write((byte)value);
        return new DefaultEbiType(writable.build());
    }

    int getId();

    class DefaultEbiType extends ImmutableGtpType<EbiType> implements EbiType {

        private DefaultEbiType(final Buffer buffer) {
            super(buffer);
        }

        @Override
        public String toString() {
            return getBuffer().toString();
        }

        @Override
        public int getId() {
            return Byte.toUnsignedInt(getBuffer().getByte(0));
        }
    }

}
