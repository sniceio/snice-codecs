package io.snice.codecs.codec.gtp.gtpc.v2.type;

import io.snice.buffer.Buffer;
import io.snice.buffer.Buffers;
import io.snice.codecs.codec.gtp.gtpc.v2.type.impl.ImmutableGtpType;

import static io.snice.preconditions.PreConditions.assertNotEmpty;

public interface ImsiType extends GtpType {

    static ImsiType parse(final Buffer buffer) {
        Buffers.assertNotEmpty(buffer, "The buffer cannot be null or empty");
        return new ImsiType.DefaultImsiType(buffer);
    }

    static ImsiType parse(final String buffer) {
        assertNotEmpty(buffer, "The buffer cannot be null or the empty string");
        return new ImsiType.DefaultImsiType(Buffers.wrapAsTbcd(buffer));
    }

    static ImsiType ofValue(final Buffer buffer) {
        return parse(buffer);
    }

    static ImsiType ofValue(final String buffer) {
        return parse(buffer);
    }

    class DefaultImsiType extends ImmutableGtpType<TbcdType> implements ImsiType {
        private DefaultImsiType(final Buffer buffer) {
            super(buffer);
        }

        @Override
        public String toString() {
            return getBuffer().toTBCD();
        }
    }
}
