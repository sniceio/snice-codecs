package io.snice.codecs.codec.gtp.gtpc.v2.tliv.impl;

import io.snice.buffer.Buffer;
import io.snice.buffer.ReadableBuffer;
import io.snice.buffer.WritableBuffer;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2InfoElement;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.BearerContext;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.FTeid;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.Imsi;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.TlivFramer;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.TypeLengthInstanceValue;
import io.snice.codecs.codec.gtp.gtpc.v2.type.GtpType;
import io.snice.codecs.codec.gtp.gtpc.v2.type.RawType;

import static io.snice.preconditions.PreConditions.assertArgument;
import static io.snice.preconditions.PreConditions.assertNotNull;

/**
 * The {@link RawTypeLengthInstanceValue} represents a framed, but otherwise unparsed,
 * TLIV. In many cases, an application dealing with GTP does not need to check every
 * IE (information element) in detail and as such, can essentially leave it alone
 * and may e.g. write it back out to socket as is (raw). If, however, the application
 * need to parse the value out, then use the {@link #ensure()} method, which
 * will force it to be fully parsed.
 */
public class RawTypeLengthInstanceValue implements TypeLengthInstanceValue<RawType> {

    private static final byte EXTENSION_TYPE = (byte) 0xFE;

    /**
     * This is the full raw buffer representing the entire TLIV. You can write
     * this out to the network and it'll be correct. I.e., this buffer contains
     * both the header as well as the actual value.
     */
    private final Buffer raw;

    /**
     * The raw value of this TLIV. I.e., without the header.
     */
    private final RawType value;

    protected RawTypeLengthInstanceValue(final Buffer raw, final RawType value) {
        this.raw = raw;
        this.value = value;
    }

    public static RawTypeLengthInstanceValue frame(final Buffer buffer) {
        assertNotNull(buffer, "The buffer cannot be null");
        return frame(buffer.toReadableBuffer());
    }

    public static RawTypeLengthInstanceValue frame(final ReadableBuffer buffer) {
        assertNotNull(buffer, "The buffer cannot be null");
        assertArgument(buffer.getReadableBytes() >= 4, "A GTPv2 TLIV has at least 4 bytes");

        final byte type = buffer.getByte(buffer.getReaderIndex());
        if (type == EXTENSION_TYPE) {
            throw new RuntimeException("Haven't implemented the extension type just yet");
        }

        // we don't support extension types so it'll always be 4. Just making
        // it super obvious
        final int headerLength = 4;
        final int length = buffer.getUnsignedShort(buffer.getReaderIndex() + 1);
        final Buffer raw = buffer.readBytes(headerLength + length);
        final Buffer value = raw.slice(headerLength, raw.capacity());

        return new RawTypeLengthInstanceValue(raw, RawType.parse(value));
    }

    public static RawTypeLengthInstanceValue create(final Gtp2InfoElement type, final GtpType value) {
        return create(type, 0, value);
    }

    public static RawTypeLengthInstanceValue create(final Gtp2InfoElement type, final int instance, final GtpType value) {
        assertNotNull(type, "The GTPv2 Information Element type cannot be null");
        assertNotNull(value, "The GTPv2 message value cannot be null");
        final var valueBuffer = value.getBuffer();
        final var raw = WritableBuffer.of(4 + valueBuffer.capacity()).fastForwardWriterIndex();
        raw.setByte(0, type.getType());
        raw.setUnsignedShort(1, valueBuffer.capacity());
        raw.setByte(3, (byte) (((byte) instance) & 0x0F));

        // TODO: use composite buffer (which doesn't exist yet)
        // dumb - really need to make that composite buffer
        for (int i = 4; i < raw.capacity(); ++i) {
            raw.setByte(i, valueBuffer.getByte(i - 4));
        }
        return new RawTypeLengthInstanceValue(raw.build(), RawType.parse(valueBuffer));
    }

    @Override
    public byte getType() {
        return raw.getByte(0);
    }

    @Override
    public RawType getValue() {
        return value;
    }

    @Override
    public int getLength() {
        return raw.getUnsignedShort(1);
    }

    @Override
    public int getInstance() {
        return raw.getByte(3) & 0x0F;
    }

    @Override
    public Buffer getRaw() {
        return raw;
    }

    /**
     * If this method actually gets called then that means that we are the
     * {@link RawTypeLengthInstanceValue} itself and that we need to frame it
     * further. Subclasses MUST override this method and simply return <code>this</code>
     *
     * @return
     */
    @Override
    public TypeLengthInstanceValue ensure() {
        return TlivFramer.frame(getType(), this);
    }

    @Override
    public Imsi toImsi() {
        return ensure().toImsi();
    }

    @Override
    public FTeid toFTeid() {
        return ensure().toFTeid();
    }

    @Override
    public BearerContext toBearerContext() {
        return ensure().toBearerContext();
    }
}
