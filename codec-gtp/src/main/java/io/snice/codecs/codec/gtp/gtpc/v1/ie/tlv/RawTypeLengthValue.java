package io.snice.codecs.codec.gtp.gtpc.v1.ie.tlv;

import io.snice.buffer.Buffer;
import io.snice.buffer.ReadableBuffer;
import io.snice.codecs.codec.gtp.gtpc.v2.type.GtpType;
import io.snice.codecs.codec.gtp.gtpc.v2.type.RawType;

import static io.snice.preconditions.PreConditions.assertArgument;
import static io.snice.preconditions.PreConditions.assertNotNull;

public class RawTypeLengthValue implements TypeLengthValue<RawType> {

    private final Buffer raw;
    private final RawType value;

    public static RawTypeLengthValue frame(final Buffer buffer) {
        assertNotNull(buffer);
        return frame(buffer.toReadableBuffer());
    }

    public static RawTypeLengthValue frame(final ReadableBuffer buffer) {
        assertArgument(buffer.getReadableBytes() >= 3, "A GTPv1 TypeLengthValue has at least 3 bytes");
        final byte type = buffer.getByte(0);

        // 2 bytes are for length
        final int length = buffer.getShort(1);

        // Read the entire buffer.
        final Buffer raw = buffer.readBytes(3 + length);

        // and slice out just the actual value, which always starts 3 bytes in
        final Buffer value = raw.slice(3, 3 + length);

        return new RawTypeLengthValue(raw, RawType.parse(value));
    }

    private RawTypeLengthValue(final Buffer raw, final RawType value) {
        this.raw = raw;
        this.value = value;

    }

    @Override
    public RawTypeLengthValue ensure() {
        return this;
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
        return raw.getShort(1);
    }

    @Override
    public Buffer getRaw() {
        return raw;
    }

}
