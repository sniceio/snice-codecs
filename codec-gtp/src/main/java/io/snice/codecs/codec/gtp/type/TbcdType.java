package io.snice.codecs.codec.gtp.type;

import io.snice.buffer.Buffer;
import io.snice.buffer.Buffers;
import io.snice.codecs.codec.gtp.type.impl.ImmutableGtpType;

import static io.snice.preconditions.PreConditions.assertNotEmpty;

/**
 * There are several types within both GTP and Diameter that are encoded according to
 *  the TBCD number format, such as IMSI and MSIDN.
 *
 *  E.g., for the IMSI (see TS 29.274 section 8.3) it states:
 *
 * Octets 5 to (n+4) represent the IMSI value in international number format as described in ITU-T Rec E.212 [64],
 * encoded as TBCD digits, i.e. digits from 0 through 9 are encoded "0000" to "1001". When there is an odd number of
 * digits, bits 8 to 5 of the last octet are encoded with the filler "1111". The maximum number of digits is 15.
 *
 * So note that the last octet is also used to determine if that last digit is to be included or not.
 * The same is true for the MSIDN.
 *
 */
public interface TbcdType extends GtpType {

    static TbcdType parse(final Buffer buffer) {
        Buffers.assertNotEmpty(buffer, "The buffer cannot be null or empty");
        return new DefaultTbcdType(buffer);
    }

    static TbcdType parse(final String buffer) {
        assertNotEmpty(buffer, "The buffer cannot be null or the empty string");
        return new DefaultTbcdType(Buffers.wrapAsTbcd(buffer));
    }

    static TbcdType ofValue(final Buffer buffer) {
        return parse(buffer);
    }

    static TbcdType ofValue(final String buffer) {
        return parse(buffer);
    }

    class DefaultTbcdType extends ImmutableGtpType<TbcdType> implements TbcdType {
        private DefaultTbcdType(final Buffer buffer) {
            super(buffer);
        }

        @Override
        public String toString() {
            return getBuffer().toTBCD();
        }
    }
}
