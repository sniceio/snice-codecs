package io.snice.codecs.codec.gtp.gtpc.v2.type;

import io.snice.buffer.Buffer;
import io.snice.buffer.WritableBuffer;
import io.snice.codecs.codec.gtp.gtpc.v2.type.impl.ImmutableGtpType;

import static io.snice.preconditions.PreConditions.assertArgument;
import static io.snice.preconditions.PreConditions.assertNotNull;

public interface PdnType extends GtpType {

    static PdnType of(final Type type) {
        assertNotNull(type, "The type cannot be null");
        return new DefaultPdnType(type);
    }

    static PdnType parse(final Buffer buffer) {
        assertArgument(buffer != null && buffer.capacity() == 1, "The buffer cannot be null and must be exactly 1 byte");
        return new DefaultPdnType(Type.lookup(buffer.getByte(0)));
    }

    static PdnType parse(final String buffer) {
        assertArgument(buffer != null && buffer.length() == 1, "The buffer cannot be null and must be exactly 1 byte");
        return new DefaultPdnType(Type.lookup((byte) (buffer.charAt(0) - '0')));
    }

    static PdnType ofValue(final Buffer buffer) {
        return parse(buffer);
    }

    static PdnType ofValue(final String buffer) {
        return parse(buffer);
    }

    static PdnType ofValue(final byte value) {
        return new DefaultPdnType(Type.lookup(value));
    }

    Type getType();

    class DefaultPdnType extends ImmutableGtpType<PdnType> implements PdnType {
        private final Type type;

        private DefaultPdnType(final Type type) {
            super(type.getBuffer());
            this.type = type;
        }

        @Override
        public String toString() {
            return type.toString();
        }

        @Override
        public Type getType() {
            return type;
        }
    }

    enum Type {

        IPv4(1),
        IPv6(2),
        IPv4v6(3);

        private final Buffer buffer;

        Type(final int value) {
            final var writable = WritableBuffer.of(1);
            writable.write((byte) value);
            this.buffer = writable.build();
        }

        static Type lookup(final int value) {
            switch (value) {
                case 1:
                    return IPv4;
                case 2:
                    return IPv6;
                case 3:
                    return IPv4v6;
                default:
                    throw new IllegalArgumentException("Unknown PDN type " + value);
            }
        }

        public Buffer getBuffer() {
            return buffer;
        }
    }
}
