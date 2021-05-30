package io.snice.codecs.codec.gtp.gtpc.v2.type;

import io.snice.buffer.Buffer;
import io.snice.codecs.codec.gtp.gtpc.v2.type.impl.ImmutableGtpType;

import static io.snice.preconditions.PreConditions.assertArgument;

/**
 * TS 29.274 section 8.4
 */
public interface CauseType extends GtpType {

    static CauseType parse(final Buffer buffer) {
        assertArgument(buffer.capacity() == 2 || buffer.capacity() == 6, "The Cause is either 2 or 6 bytes long");

        final byte cause = buffer.getByte(0);

        // TODO: the rest

        return new DefaultCauseType(buffer, cause);
    }

    static CauseType parse(final String buffer) {
        assertArgument(buffer != null && buffer.length() == 21, "The buffer must be exactly 21 bytes long");
        return null;
    }

    static CauseType ofValue(final Buffer buffer) {
        return parse(buffer);
    }

    static CauseType ofValue(final String buffer) {
        return parse(buffer);
    }

    int getCauseValue();

    class DefaultCauseType extends ImmutableGtpType<CauseType> implements CauseType {

        private final byte cause;

        private DefaultCauseType(final Buffer buffer, final byte cause) {
            super(buffer);
            this.cause = cause;
        }

        @Override
        public int getCauseValue() {
            return cause;
        }
    }

}
