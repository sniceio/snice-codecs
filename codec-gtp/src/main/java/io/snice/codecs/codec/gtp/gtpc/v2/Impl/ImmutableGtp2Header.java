package io.snice.codecs.codec.gtp.gtpc.v2.Impl;

import io.snice.buffer.Buffer;
import io.snice.buffer.Buffers;
import io.snice.buffer.ReadableBuffer;
import io.snice.buffer.WritableBuffer;
import io.snice.codecs.codec.gtp.GtpVersionException;
import io.snice.codecs.codec.gtp.Teid;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Header;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2HeaderBuilder;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2MessageType;
import io.snice.preconditions.PreConditions;

import java.util.Optional;

import static io.snice.preconditions.PreConditions.assertArgument;
import static io.snice.preconditions.PreConditions.assertNotNull;

public class ImmutableGtp2Header implements Gtp2Header {

    /**
     * Contains the entire raw GTPv2 header, which is always 8 or 12
     * bytes long, depending on whether the TEID is present or not.
     * <p>
     * And the f@#$4 dumb thing is that instead of having the optional
     * TEID at the end of those bytes, it is in the middle, which means that
     * the sequence no etc, which are always present, are now in different
     * directions. The people that defines standards are certainly not
     * developers!
     */
    private final Buffer header;
    private final Gtp2MessageType type;
    private final Optional<Teid> teid;
    private final Buffer seqNo;

    public static Gtp2HeaderBuilder of(final Gtp2MessageType type) {
        assertNotNull(type, "The type cannot be null");
        return new ImmutableGtp2HeaderBuilder(type);
    }

    @Override
    public Gtp2MessageType getType() {
        return type;
    }

    @Override
    public Gtp2HeaderBuilder copy() {
        // TODO: can do more efficiently but is good enough for now.
        final var builder = of(type);
        teid.ifPresent(builder::withTeid);
        builder.withSequenceNumber(seqNo);
        builder.withTlivSize(getBodyLength());
        return builder;
    }

    /**
     * Frame the buffer into a {@link Gtp2Header}. A {@link Gtp2Header} is either 8 or 12 bytes long
     * depending if the TEID is present or not.
     *
     * @param buffer
     * @return
     */
    public static Gtp2Header frame(final ReadableBuffer buffer) {
        assertNotNull(buffer, "The buffer cannot be null");
        assertArgument(buffer.capacity() >= 8, "The minimum no of bytes for a GTP header is 8 bytes. This buffer contains less");

        final byte flags = buffer.getByte(0);
        final int version = (flags & 0b11100000) >> 5;
        if (version != 2) {
            throw new GtpVersionException(2, version);
        }

        final boolean teidFlag = (flags & 0b00001000) == 0b00001000;
        final Buffer header = teidFlag ? buffer.readBytes(12) : buffer.readBytes(8);

        final Optional<Teid> teid;
        if (teidFlag) {
            teid = Optional.of(Teid.of(header.slice(4, 8)));
        } else {
            teid = Optional.empty();
        }

        final Buffer seqNo = teidFlag ? header.slice(8, 11) : header.slice(4, 7);
        final Gtp2MessageType type = Gtp2MessageType.lookup(Byte.toUnsignedInt(header.getByte(1)));
        return new ImmutableGtp2Header(header, type, teid, seqNo);
    }

    private ImmutableGtp2Header(final Buffer header, final Gtp2MessageType type, final Optional<Teid> teid, final Buffer seqNo) {
        this.header = header;
        this.type = type;
        this.teid = teid;
        this.seqNo = seqNo;
    }

    @Override
    public int getLength() {
        return header.getUnsignedShort(2);
    }

    @Override
    public int getBodyLength() {
        return getLength() - header.capacity() + 4;
    }

    @Override
    public int getTotalLength() {
        return getLength() + 4;
    }

    @Override
    public int getHeaderLength() {
        return header.capacity();
    }

    @Override
    public int getMessageTypeDecimal() {
        return Byte.toUnsignedInt(header.getByte(1));
    }

    @Override
    public Buffer getBuffer() {
        return header;
    }

    @Override
    public Optional<Teid> getTeid() {
        return teid;
    }

    @Override
    public Buffer getSequenceNo() {
        return seqNo;
    }

    @Override
    public int getSequenceNoAsDecimal() {
        return seqNo.getIntFromThreeOctets(0);
    }

    private static class ImmutableGtp2HeaderBuilder implements Gtp2HeaderBuilder {

        private Gtp2MessageType type;
        private Teid teid;
        private Buffer seqNo;
        private int tlivSize;

        private ImmutableGtp2HeaderBuilder(final Gtp2MessageType type) {
            this.type = type;
        }

        @Override
        public Gtp2HeaderBuilder withType(final byte type) {
            this.type = Gtp2MessageType.lookup(type);
            return this;
        }

        @Override
        public Gtp2HeaderBuilder withType(final Gtp2MessageType type) {
            PreConditions.assertNotNull(type, "The GTPv2 Message Type cannot be null");
            this.type = type;
            return this;
        }

        @Override
        public Gtp2HeaderBuilder withTeid(final Teid teid) {
            assertNotNull(teid, "If you specify the TEID, it cannot be null");
            this.teid = teid;
            return this;
        }

        @Override
        public Gtp2HeaderBuilder withSequenceNumber(final Buffer seqNo) {
            assertArgument(seqNo != null && seqNo.capacity() == 3, "The Sequence Number must be exactly 3 bytes long");
            this.seqNo = seqNo;
            return this;
        }

        @Override
        public Gtp2HeaderBuilder withTlivSize(final int size) {
            assertArgument(size >= 0, "The size of the TLIVs cannot be negative");
            this.tlivSize = size;
            return this;
        }

        @Override
        public Gtp2Header build() {
            final WritableBuffer header = teid != null ? WritableBuffer.of(12) : WritableBuffer.of(8);
            header.fastForwardWriterIndex(); // we'll manually flip things...
            header.setBit6(0, true); // GTPv2 - top 3 (5, 6, 7) bits of the first byte is encoded as 010 for GTPv2


            final int finalSize;
            if (teid != null) {
                header.setBit3(0, true); // TEID flag
                teid.setAtIndex(4, header);
                finalSize = tlivSize + 3 + 1 + 4; // +3 for seqNo, +1 for spare and +4 for TEID
            } else {
                finalSize = tlivSize + 3 + 1; // no TEID so only +3 for seqNo and +1 for spare
            }
            header.setUnsignedShort(2, finalSize);

            // MP flag - bit 2: we don't support right now.
            // TODO: support message priority

            header.setByte(1, (byte) type.getType());
            final var actualSeqNo = setSeqNo(header);
            return new ImmutableGtp2Header(header.build(), type, Optional.ofNullable(teid), actualSeqNo);
        }

        private Buffer setSeqNo(final WritableBuffer buffer) {
            final int offset = teid == null ? 0 : 4;
            final var seqNo = this.seqNo != null ? this.seqNo : Buffers.wrap((byte) 0x00, (byte) 0x01, (byte) 0x02);
            buffer.setByte(4 + offset, seqNo.getByte(0));
            buffer.setByte(5 + offset, seqNo.getByte(1));
            buffer.setByte(6 + offset, seqNo.getByte(2));
            return seqNo;
        }
    }

}
