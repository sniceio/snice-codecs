package io.snice.codecs.codec.gtp.gtpc.v2.type;

import io.snice.buffer.Buffer;
import io.snice.buffer.Buffers;
import io.snice.codecs.codec.gtp.gtpc.v2.type.impl.ImmutableGtpType;

import static io.snice.preconditions.PreConditions.assertNotNull;

/**
 * There are several Information Elements that are essentially just
 * Strings. This one follows the pattern of an Octet string in Diameter
 */
public interface OctetStringType extends GtpType {

    static OctetStringType parse(final Buffer buffer) {
        assertNotNull(buffer, "The buffer cannot be null");
        return new DefaultOctetStringType(buffer);
    }

    static OctetStringType parse(final String buffer) {
        assertNotNull(buffer, "The buffer cannot be null");
        return new DefaultOctetStringType(Buffers.wrap(buffer));
    }

    static OctetStringType ofValue(final Buffer buffer) {
        return parse(buffer);
    }

    static OctetStringType ofValue(final String buffer) {
        return parse(buffer);
    }

    class DefaultOctetStringType extends ImmutableGtpType<OctetStringType> implements OctetStringType {
        private DefaultOctetStringType(final Buffer buffer) {
            super(buffer);
        }

        @Override
        public String toString() {
            return getBuffer().toUTF8String();
        }
    }
}
