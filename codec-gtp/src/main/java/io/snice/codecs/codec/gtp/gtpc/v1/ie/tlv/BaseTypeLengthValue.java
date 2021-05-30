package io.snice.codecs.codec.gtp.gtpc.v1.ie.tlv;

import io.snice.buffer.Buffer;
import io.snice.codecs.codec.gtp.gtpc.v2.type.GtpType;

public abstract class BaseTypeLengthValue<T extends GtpType> implements TypeLengthValue<T> {

    private final byte type;
    private final RawTypeLengthValue raw;
    private final T value;

    protected BaseTypeLengthValue(final byte type, final T value, final RawTypeLengthValue raw) {
        this.type = type;
        this.raw = raw;
        this.value = value;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public byte getType() {
        return type;
    }

    @Override
    public Buffer getRaw() {
        return raw.getRaw();
    }

    @Override
    public int getLength() {
        return raw.getLength();
    }

}
