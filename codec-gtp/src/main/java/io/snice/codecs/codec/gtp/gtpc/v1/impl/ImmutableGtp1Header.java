package io.snice.codecs.codec.gtp.gtpc.v1.impl;

import io.snice.buffer.Buffer;
import io.snice.buffer.Buffers;
import io.snice.buffer.ReadableBuffer;
import io.snice.buffer.WritableBuffer;
import io.snice.codecs.codec.gtp.GtpVersionException;
import io.snice.codecs.codec.gtp.Teid;
import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1Header;
import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1HeaderBuilder;
import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1MessageType;

import java.util.Optional;

import static io.snice.preconditions.PreConditions.assertArgument;
import static io.snice.preconditions.PreConditions.assertNotNull;

public class ImmutableGtp1Header implements Gtp1Header {

    private final Buffer header;
    private final Gtp1MessageType type;
    private final Teid teid;
    private final Optional<Buffer> seqNo;

    private ImmutableGtp1Header(final Buffer header, final Gtp1MessageType type, final Teid teid, final Optional<Buffer> seqNo) {
        this.header = header;
        this.teid = teid;
        this.seqNo = seqNo;
        this.type = type;
    }

    public static Gtp1HeaderBuilder of(final Gtp1MessageType type) {
        assertNotNull(type, "The GTPv1 Message Type cannot be null");
        return new Builder(type);
    }

    @Override
    public Gtp1HeaderBuilder copy() {
        final var builder = of(type);
        builder.withTeid(teid);
        seqNo.ifPresent(builder::withSequenceNumber);
        return builder;
    }

    /**
     * Frame the buffer into a {@link Gtp1Header}. A {@link Gtp1Header} is either 8 or 12 bytes long
     * depending if any of the optional sequence no, extension header or n-pdu flags are present. Note
     * that even if a single one of those flags are present, the header will be an extra 4 bytes long because,
     * according to TS 29.274 section 5.1:
     * <p>
     * "Control Plane GTP header length shall be a multiple of 4 octets"
     *
     * @param buffer
     * @return
     */
    public static Gtp1Header frame(final ReadableBuffer buffer) {
        assertNotNull(buffer, "The buffer cannot be null");
        assertArgument(buffer.capacity() >= 8, "The minimum no of bytes for a GTP header is 8 bytes. This buffer contains less");

        final byte flags = buffer.getByte(0);
        final int version = (flags & 0b11100000) >> 5;
        if (version != 1) {
            throw new GtpVersionException(1, version);
        }

        // if any of the sequence no, extension or n-pdu flags are "on" then we have an additional
        // 4 bytes in the header, hence, a "long" header
        final boolean nPduNoFlag = (flags & 0b00000100) == 0b00000100;
        final boolean seqNoFlag = (flags & 0b00000010) == 0b00000010;
        final boolean extHeaderFlag = (flags & 0b00000001) == 0b00000001;
        final boolean longHeader = seqNoFlag || nPduNoFlag || extHeaderFlag;

        final Buffer header = longHeader ? buffer.readBytes(12) : buffer.readBytes(8);
        final Teid teid = Teid.of(header.slice(4, 8));
        final Optional<Buffer> seqNo = seqNoFlag ? Optional.of(header.slice(8, 10)) : Optional.empty();
        final Gtp1MessageType type = Gtp1MessageType.lookup(Byte.toUnsignedInt(header.getByte(1)));

        return new ImmutableGtp1Header(header, type, teid, seqNo);
    }

    @Override
    public Teid getTeid() {
        return teid;
    }

    @Override
    public Gtp1MessageType getType() {
        return type;
    }

    @Override
    public Optional<Buffer> getSequenceNo() {
        return seqNo;
    }

    @Override
    public Optional<Integer> getSequenceNoAsDecimal() {
        return seqNo.map(b -> b.getUnsignedShort(0));
    }

    @Override
    public Buffer getBuffer() {
        return header;
    }

    @Override
    public int getLength() {
        return header.getUnsignedShort(2);
    }

    @Override
    public int getHeaderLength() {
        return header.capacity();
    }

    @Override
    public int getBodyLength() {
        return getLength() - header.capacity() + 8;
    }

    @Override
    public int getTotalLength() {
        return getLength() + 8;
    }

    @Override
    public int getMessageTypeDecimal() {
        return Byte.toUnsignedInt(header.getByte(1));
    }

    private static class Builder implements Gtp1HeaderBuilder {

        private Gtp1MessageType type;
        private int size = 0;
        private Buffer seqNo;
        private Teid teid;

        private Builder(final Gtp1MessageType type) {
            this.type = type;
        }

        @Override
        public Gtp1HeaderBuilder withTeid(final Teid teid) {
            assertNotNull(teid, "The TEID cannot be null");
            this.teid = teid;
            return this;
        }

        @Override
        public Gtp1HeaderBuilder withTeid(final Buffer teid) {
            this.teid = Teid.of(teid);
            return this;
        }

        @Override
        public Gtp1HeaderBuilder withSequenceNumber(final Buffer seqNo) {
            Buffers.assertBufferCapacity(seqNo, 2, "The Sequence Number must be exactly 2 bytes");
            this.seqNo = seqNo;
            return this;
        }

        @Override
        public Gtp1HeaderBuilder withType(final Gtp1MessageType type) {
            assertNotNull(type);
            this.type = type;
            return this;
        }

        @Override
        public Gtp1HeaderBuilder withBodySize(final int size) {
            assertArgument(size >= 0, "The size must be greater than zero");
            this.size = size;
            return this;
        }

        @Override
        public Gtp1Header build() {
            assertNotNull(teid, "The TEID cannot be null");
            final var longHeader = seqNo != null;
            final var writable = WritableBuffer.of(8 + (longHeader ? 4 : 0)).fastForwardWriterIndex();
            writable.setBit5(0, true); // GTP version 1
            writable.setBit4(0, true); // GTP as opposed to GTP Prime (when it is set to zero)

            if (seqNo != null) {
                writable.setBit1(0, true);
                writable.setByte(8, seqNo.getByte(0));
                writable.setByte(9, seqNo.getByte(1));
            }

            if (teid != null) {
                final var teidBuffer = teid.getBuffer();
                writable.setByte(4, teidBuffer.getByte(0));
                writable.setByte(5, teidBuffer.getByte(1));
                writable.setByte(6, teidBuffer.getByte(2));
                writable.setByte(7, teidBuffer.getByte(3));
            }

            writable.setByte(1, (byte) type.getType());

            final var finalSize = size + (longHeader ? 4 : 0);
            writable.setUnsignedShort(2, finalSize);
            return new ImmutableGtp1Header(writable.build(), type, teid, Optional.ofNullable(seqNo));
        }
    }

}
