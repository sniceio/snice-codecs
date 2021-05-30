package io.snice.codecs.codec.gtp.gtpc.v1.ie.tv;

import io.snice.buffer.Buffer;
import io.snice.buffer.ReadableBuffer;
import io.snice.codecs.codec.gtp.gtpc.InfoElement;
import io.snice.codecs.codec.gtp.gtpc.v2.type.RawType;

import static io.snice.preconditions.PreConditions.assertArgument;
import static io.snice.preconditions.PreConditions.assertNotNull;

/**
 * The {@link RawTypeValue} represents a framed, but otherwise unparsed,
 * TV. In many cases, an application dealing with GTP does not need to check every
 * IE (information element) in detail and as such, can essentially leave it alone
 * and may e.g. write it back out to socket as is (raw). If, however, the application
 * need to parse the value out, then use the {@link #ensure()} method, which
 * will force it to be fully parsed.
 * <p>
 * NOTE: not sure a {@link RawType} is needed for TV's since we MUST know their
 * length, which isn't encoded into the actual TV header (because there essentially
 * isn't one) and as such, each type must be specified and if you must specify it, you
 * might as well just specify it completely... For now, we'll keep it around.
 */
public class RawTypeValue implements TypeValue<RawType> {

    /**
     * This is the full raw buffer representing the entire TV. You can write
     * this out to the network and it'll be correct. I.e., this buffer contains
     * both the header as well as the actual value.
     */
    private final Buffer raw;

    /**
     * The raw value of this TV. I.e., without the header.
     */
    private final RawType value;

    private RawTypeValue(final Buffer raw, final RawType value) {
        this.raw = raw;
        this.value = value;
    }

    public static RawTypeValue frame(final Buffer buffer) {
        assertNotNull(buffer);
        return frame(buffer.toReadableBuffer());
    }

    public static RawTypeValue frame(final ReadableBuffer buffer) {
        assertArgument(buffer.getReadableBytes() >= 2, "A GTPv1 TypeValue has at least 2 bytes");
        final byte type = buffer.peekByte();
        // TODO: generate this and add this getLength lookup in the actual Gtp1InfoElement instead.
        //      and the lookup should just return the actual element as an optional.
        final int length = TypeValueFramer.getLength(type);
        final var raw = buffer.readBytes(1 + length);
        final Buffer value = raw.slice(1, 1 + length);
        return new RawTypeValue(raw, RawType.parse(value));
    }

    public InfoElement ensure() {
        return TypeValueFramer.frame(this);
    }

    @Override
    public RawType getValue() {
        return value;
    }

    @Override
    public int getLength() {
        return raw.capacity() - 1; // minus 1 since that's the type. The rest is the actual value.
    }

    @Override
    public byte getType() {
        return raw.getByte(0);
    }

    @Override
    public Buffer getRaw() {
        return raw;
    }
}
