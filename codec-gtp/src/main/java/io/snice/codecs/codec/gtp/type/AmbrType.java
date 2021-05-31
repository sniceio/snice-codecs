package io.snice.codecs.codec.gtp.type;

import io.snice.buffer.Buffer;
import io.snice.buffer.Buffers;
import io.snice.buffer.WritableBuffer;
import io.snice.codecs.codec.gtp.type.impl.ImmutableGtpType;

public interface AmbrType extends GtpType {

    static AmbrType parse(final Buffer buffer) {
        Buffers.assertBufferCapacity(buffer, 8, "The AMBR is exactly 8 bytes long");
        final long maxUplink = buffer.getUnsignedInt(0);
        final long maxDownlink = buffer.getUnsignedInt(4);
        return new DefaultAmbrType(buffer, maxUplink, maxDownlink);
    }

    static AmbrType parse(final String buffer) {
        return parse(Buffers.wrap(buffer));
    }

    static AmbrType ofValue(final Buffer buffer) {
        return parse(buffer);
    }

    static AmbrType ofValue(final String buffer) {
        return parse(buffer);
    }

    static AmbrType ofValue(final long maxUplink, final long maxDownlink) {
        final var writable = WritableBuffer.of(8).fastForwardWriterIndex();
        writable.setUnsignedInt(0, maxUplink);
        writable.setUnsignedInt(4, maxDownlink);
        return new DefaultAmbrType(writable.build(), maxUplink, maxDownlink);
    }

    long getMaxUplink();

    long getMaxDownlink();

    class DefaultAmbrType extends ImmutableGtpType<AmbrType> implements AmbrType {

        private final long maxUplink;
        private final long maxDownlink;

        private DefaultAmbrType(final Buffer buffer, final long maxUplink, final long maxDownlink) {
            super(buffer);
            this.maxUplink = maxUplink;
            this.maxDownlink = maxDownlink;
        }

        @Override
        public long getMaxUplink() {
            return maxUplink;
        }

        @Override
        public long getMaxDownlink() {
            return maxDownlink;
        }
    }

}
