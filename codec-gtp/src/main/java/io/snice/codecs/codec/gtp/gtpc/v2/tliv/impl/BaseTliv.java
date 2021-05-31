package io.snice.codecs.codec.gtp.gtpc.v2.tliv.impl;

import io.snice.buffer.Buffer;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.TypeLengthInstanceValue;
import io.snice.codecs.codec.gtp.type.GtpType;

import java.util.Objects;

/**
 * Base class for all fully framed/parsed GTPv2 Information Elements.
 */
public abstract class BaseTliv<T extends GtpType> implements TypeLengthInstanceValue<T> {

    private final RawTypeLengthInstanceValue raw;
    private final T value;

    protected BaseTliv(final T value, final RawTypeLengthInstanceValue raw) {
        this.raw = raw;
        this.value = value;
    }

    @Override
    public byte getType() {
        return raw.getType();
    }

    @Override
    public int getLength() {
        return raw.getLength();
    }

    @Override
    public int getInstance() {
        return raw.getInstance();
    }

    @Override
    public Buffer getRaw() {
        return raw.getRaw();
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final BaseTliv<?> baseTliv = (BaseTliv<?>) o;
        return value.equals(baseTliv.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
