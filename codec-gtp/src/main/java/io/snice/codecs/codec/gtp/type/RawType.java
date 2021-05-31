package io.snice.codecs.codec.gtp.type;

import io.snice.buffer.Buffer;
import io.snice.buffer.Buffers;
import io.snice.codecs.codec.gtp.type.impl.ImmutableGtpType;

import static io.snice.preconditions.PreConditions.assertNotNull;

/**
 * Represents a type for which we do not have a specific {@link GtpType} specified.
 * It is up to the application to parse and interpret this one.
 */
public interface RawType extends GtpType {

    static RawType parse(final Buffer buffer) {
        assertNotNull(buffer, "The buffer cannot be null");
        return new RawTypeImpl(buffer);
    }

    static RawType parse(final String buffer) {
        assertNotNull(buffer, "The buffer cannot be null");
        return new RawTypeImpl(Buffers.wrap(buffer));
    }

    static RawType ofValue(final Buffer buffer) {
        return parse(buffer);
    }

    static RawType ofValue(final String buffer) {
        return parse(buffer);
    }

    class RawTypeImpl extends ImmutableGtpType<RawType> implements RawType {
        private RawTypeImpl(final Buffer buffer) {
            super(buffer);
        }
    }
}
