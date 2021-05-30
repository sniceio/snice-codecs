package io.snice.codecs.codec.gtp.gtpc.v2.tliv;

import io.snice.buffer.Buffer;
import io.snice.buffer.ReadableBuffer;
import io.snice.codecs.codec.gtp.gtpc.InfoElement;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.impl.RawTypeLengthInstanceValue;
import io.snice.codecs.codec.gtp.gtpc.v2.type.GtpType;
import io.snice.codecs.codec.gtp.gtpc.v2.type.RawType;

/**
 * In GTPv2, all {@link InfoElement}s are of so-called TLIV - Type, Length, Instance, Value.
 */
public interface TypeLengthInstanceValue <T extends GtpType> extends InfoElement<T> {

    String CANNOT_CAST_IE_OF_TYPE = "Cannot cast Info Element of type ";

    static TypeLengthInstanceValue<RawType> frame(final Buffer buffer) {
        return RawTypeLengthInstanceValue.frame(buffer);
    }

    static TypeLengthInstanceValue<RawType> frame(final ReadableBuffer buffer) {
        return RawTypeLengthInstanceValue.frame(buffer);
    }

    /**
     * Get the size in bytes for this entire TLIV, which includes the header and the actual
     * value. This is just a convenience method for checking the length of the underlying {@link Buffer}.
     *
     * @return the total length in bytes for this TLIV
     */
    @Override
    default int getTotalSize() {
        return getRaw().capacity();
    }

    @Override
    default boolean isTypeLengthInstanceValue() {
        return true;
    }

    @Override
    default TypeLengthInstanceValue<T> toTliv() throws ClassCastException {
        return this;
    }

    // <V extends TypeLengthInstanceValue<T>> V ensure();

    default boolean isImsi() {
        return getTypeAsDecimal() == 1;
    }

    default Imsi toImsi() {
        throw new ClassCastException(CANNOT_CAST_IE_OF_TYPE + getClass().getName()
                + " to type " + Imsi.class.getName());
    }

    default boolean isFTeid() {
        return getTypeAsDecimal() == FTeid.TYPE_VALUE;
    }

    default FTeid toFTeid() {
        throw new ClassCastException(CANNOT_CAST_IE_OF_TYPE + getClass().getName()
                + " to type " + FTeid.class.getName());
    }

    default boolean isBearerContext() {
        return getTypeAsDecimal() == BearerContext.TYPE_VALUE;
    }

    default BearerContext toBearerContext() {
        throw new ClassCastException(CANNOT_CAST_IE_OF_TYPE + getClass().getName()
                + " to type " + BearerContext.class.getName());
    }
}
