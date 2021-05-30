package io.snice.codecs.codec.gtp.gtpc.v2.type;

import io.snice.buffer.Buffer;
import io.snice.buffer.WritableBuffer;
import io.snice.codecs.codec.gtp.gtpc.v2.type.impl.ImmutableGtpType;

import static io.snice.preconditions.PreConditions.assertArgument;

/**
 * There are a few different QoS types in GTPv2. They are similar but
 * not 100% the same. E.g. Flow QoS is exactly this {@link QosType}. However,
 * Bearer QoS has an extra initial byte with some additional info.
 */
public interface QosType extends GtpType {

    static QosType parse(final Buffer buffer) {
        assertArgument(buffer != null && buffer.capacity() == 21, "The buffer must be exactly 21 bytes long");
        final int qci = Byte.toUnsignedInt(buffer.getByte(0));
        final long mbrUplink = buffer.getLongFromFiveOctets(1 + 0 * 5);
        final long mbrDownlink = buffer.getLongFromFiveOctets(1 + 1 * 5);
        final long gbrUplink = buffer.getLongFromFiveOctets(1 + 2 * 5);
        final long gbrDownlink = buffer.getLongFromFiveOctets(1 + 3 * 5);
        return new DefaultQosType(buffer, qci, mbrUplink, mbrDownlink, gbrUplink, gbrDownlink);
    }

    static QosType parse(final String buffer) {
        assertArgument(buffer != null && buffer.length() == 21, "The buffer must be exactly 21 bytes long");
        return null;
    }

    static QosType ofValue(final Buffer buffer) {
        return parse(buffer);
    }

    static QosType ofValue(final String buffer) {
        return parse(buffer);
    }

    static Builder ofQci(final int qci) {
        assertArgument(qci >= 0 && qci < 16, "The QCI value must be between 0-15");
        return new Builder(qci);
    }

    int getQci();

    long getMbrUplink();

    long getMbrDownlink();

    long getGbrUplink();

    long getGbrDownlink();

    class Builder {
        final int qci;
        long mbrUplink;
        long mbrDownlink;
        long gbrUplink;
        long gbrDownlink;

        private Builder(final int qci) {
            this.qci = qci;
        }

        public Builder withMbrUplink(final long value) {
            this.mbrUplink = value;
            return this;
        }

        public Builder withMbrDownlink(final long value) {
            this.mbrDownlink = value;
            return this;
        }

        public Builder withGbrUplink(final long value) {
            this.gbrUplink = value;
            return this;
        }

        public Builder withGbrDownlink(final long value) {
            this.gbrDownlink = value;
            return this;
        }

        public QosType build() {
            final var writable = WritableBuffer.of(21);
            writable.write((byte) qci);
            writable.writeFiveOctets(mbrUplink);
            writable.writeFiveOctets(mbrDownlink);
            writable.writeFiveOctets(gbrUplink);
            writable.writeFiveOctets(gbrDownlink);
            return new DefaultQosType(writable.build(), qci, mbrUplink, mbrDownlink, gbrUplink, gbrDownlink);
        }
    }

    class DefaultQosType extends ImmutableGtpType<QosType> implements QosType {

        private final int qci;
        private final long mbrUplink;
        private final long mbrDownlink;
        private final long gbrUplink;
        private final long gbrDownlink;

        private DefaultQosType(final Buffer buffer, final int qci,
                               final long mbrUplink, final long mbrDownlink,
                               final long gbrUplink, final long gbrDownlink) {
            super(buffer);
            this.qci = qci;
            this.mbrUplink = mbrUplink;
            this.mbrDownlink = mbrDownlink;
            this.gbrUplink = gbrUplink;
            this.gbrDownlink = gbrDownlink;
        }

        @Override
        public int getQci() {
            return qci;
        }

        @Override
        public long getMbrUplink() {
            return mbrUplink;
        }

        @Override
        public long getMbrDownlink() {
            return mbrDownlink;
        }

        @Override
        public long getGbrUplink() {
            return gbrUplink;
        }

        @Override
        public long getGbrDownlink() {
            return gbrDownlink;
        }
    }
}