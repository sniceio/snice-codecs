package io.snice.codecs.codec.gtp.type;

import io.snice.buffer.Buffer;
import io.snice.buffer.Buffers;
import io.snice.codecs.codec.gtp.type.impl.ImmutableGtpType;

import static io.snice.preconditions.PreConditions.assertArgument;

public interface SelectionModeType extends GtpType {

    static SelectionModeType parse(final Buffer buffer) {
        assertArgument(buffer != null && buffer.capacity() == 1, "The Selection Mode Type is only one byte long");
        final int mode = buffer.getByte(0) & 0b00000011;
        return new DefaultSelectionModeType(buffer, mode);
    }

    static SelectionModeType parse(final String buffer) {
        return parse(Buffers.wrap(buffer));
    }

    static SelectionModeType ofValue(final Buffer buffer) {
        return parse(buffer);
    }

    static SelectionModeType ofValue(final String buffer) {
        return parse(buffer);
    }

    /**
     *
     * Note that even though the specification says that the only valid values are 0-3, this library does not
     * enforce it by default. The reason being is that it should be possible to build test tools using
     * this Snice GTP Codec library and as such, it allows you to send "bad" values.
     *
     */
    static SelectionModeType ofValue(final int value) {
        assertArgument(value >= 0 && value < 16, "The Selection Mode value must be between 0-15");
        final var buffer = Buffers.wrap((byte) (value & 0b00000011));
        return new DefaultSelectionModeType(buffer, value);
    }

    int getMode();

    class DefaultSelectionModeType extends ImmutableGtpType<SelectionModeType> implements SelectionModeType {

        private final int value;

        private DefaultSelectionModeType(final Buffer buffer, final int value) {
            super(buffer);
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @Override
        public int getMode() {
            return value;
        }
    }

}
