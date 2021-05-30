package io.snice.codecs.codec.gtp.gtpc.v1.ie.tv;

import io.snice.buffer.Buffer;
import io.snice.codecs.codec.gtp.gtpc.v2.type.GtpType;

public abstract class BaseTypeValue<T extends GtpType> implements TypeValue<T> {

    private final byte type;
    private final T value;
    private final RawTypeValue raw;


    protected BaseTypeValue(final byte type, final T value, final RawTypeValue raw) {
        this.type = type;
        this.value = value;
        this.raw = raw;
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
