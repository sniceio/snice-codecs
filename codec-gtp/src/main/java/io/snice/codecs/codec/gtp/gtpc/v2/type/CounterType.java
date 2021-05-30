package io.snice.codecs.codec.gtp.gtpc.v2.type;

import io.snice.buffer.Buffer;
import io.snice.buffer.Buffers;
import io.snice.codecs.codec.gtp.gtpc.v2.type.impl.ImmutableGtpType;

import static io.snice.preconditions.PreConditions.assertArgument;

/**
 * Represents those TLIV types that are a single byte (one octet) long counter value.
 */
public interface CounterType extends GtpType {

    static CounterType parse(final Buffer buffer) {
        assertArgument(buffer != null && buffer.capacity() == 1, "The buffer must be exactly 1 byte long");
        return new DefaultCounterType(buffer);
    }

    static CounterType parse(final String buffer) {
        assertArgument(buffer != null && buffer.length() == 1, "The buffer must be exactly 1 byte long");
        try {
            final int value = Integer.valueOf(buffer);
            return new DefaultCounterType((byte) value);
        } catch (final NumberFormatException e) {
            throw new IllegalArgumentException("The given string was not a number");
        }
    }

    static CounterType ofValue(final Buffer buffer) {
        return parse(buffer);
    }

    static CounterType ofValue(final String buffer) {
        return parse(buffer);
    }

    static CounterType ofValue(final int value) {
        return new DefaultCounterType((byte) value);
    }

    int getCounter();

    class DefaultCounterType extends ImmutableGtpType<CounterType> implements CounterType {
        private final int counter;

        private DefaultCounterType(final byte b) {
            super(Buffers.wrap(b));
            this.counter = Byte.toUnsignedInt(b);
        }

        private DefaultCounterType(final Buffer buffer) {
            super(buffer);
            this.counter = Byte.toUnsignedInt(buffer.getByte(0));
        }

        @Override
        public String toString() {
            return String.valueOf(counter);
        }

        @Override
        public int getCounter() {
            return counter;
        }
    }
}
