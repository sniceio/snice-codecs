package io.snice.codecs.codec.gtp.type;

import io.snice.buffer.Buffer;
import io.snice.codecs.codec.gtp.type.impl.ImmutableGtpType;

import static io.snice.preconditions.PreConditions.assertArgument;
import static io.snice.preconditions.PreConditions.assertNotNull;

/**
 * TS 29.274 release 14 section 8.17
 */
public interface RatType extends GtpType {

    RatType UTRAN = RatType.of(Type.UTRAN);
    RatType GERAN = RatType.of(Type.GERAN);
    RatType WLAN = RatType.of(Type.WLAN);
    RatType GAN = RatType.of(Type.GAN);
    RatType HSPA_EVOLUTION = RatType.of(Type.HSPA_EVOLUTION);
    RatType EUTRAN = RatType.of(Type.EUTRAN);
    RatType VIRTUAL = RatType.of(Type.VIRTUAL);
    RatType EUTRAN_NB_IOT = RatType.of(Type.EUTRAN_NB_IOT);

    static RatType of(final Type type) {
        assertNotNull(type, "The type cannot be null");
        return new DefaultRatType(type);
    }

    static RatType of(final int type) {
        switch (type) {
            case (byte) 1:
                return UTRAN;
            case (byte) 2:
                return GERAN;
            case (byte) 3:
                return WLAN;
            case (byte) 4:
                return GAN;
            case (byte) 5:
                return HSPA_EVOLUTION;
            case (byte) 6:
                return EUTRAN;
            case (byte) 7:
                return VIRTUAL;
            case (byte) 8:
                return EUTRAN_NB_IOT;
            default:
                throw new IllegalArgumentException("Unknown RAT type " + type);
        }
    }

    static RatType parse(final Buffer buffer) {
        assertArgument(buffer != null && buffer.capacity() == 1, "The buffer cannot be null and must be exactly 1 byte");
        return of(buffer.getByte(0));
    }

    static RatType parse(final String buffer) {
        assertArgument(buffer != null && buffer.length() == 1, "The buffer cannot be null and must be exactly 1 byte");
        return of((buffer.charAt(0) - '0'));
    }

    static RatType ofValue(final Buffer buffer) {
        return parse(buffer);
    }

    static RatType ofValue(final String buffer) {
        return parse(buffer);
    }

    Type getType();

    class DefaultRatType extends ImmutableGtpType<RatType> implements RatType {
        private final Type type;

        private DefaultRatType(final Type type) {
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

        UTRAN((byte) 1),
        GERAN((byte) 2),
        WLAN((byte) 3),
        GAN((byte) 4),
        HSPA_EVOLUTION((byte) 5),
        EUTRAN((byte) 6),
        VIRTUAL((byte) 7),
        EUTRAN_NB_IOT((byte) 8);

        private final byte value;
        private final Buffer buffer;

        Type(final byte value) {
            this.value = value;
            this.buffer = Buffer.of(value);
        }

        public static Type lookup(final byte value) {
            switch (value) {
                case (byte) 1:
                    return UTRAN;
                case (byte) 2:
                    return GERAN;
                case (byte) 3:
                    return WLAN;
                case (byte) 4:
                    return GAN;
                case (byte) 5:
                    return HSPA_EVOLUTION;
                case (byte) 6:
                    return EUTRAN;
                case (byte) 7:
                    return VIRTUAL;
                case (byte) 8:
                    return EUTRAN_NB_IOT;
                default:
                    throw new IllegalArgumentException("Unknown RAT type " + value);
            }
        }

        public Buffer getBuffer() {
            return buffer;
        }
    }
}
