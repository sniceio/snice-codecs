package io.snice.codecs.codec.gtp.gtpc;

import io.snice.buffer.Buffer;
import io.snice.buffer.Buffers;
import io.snice.codecs.codec.gtp.gtpc.v1.ie.tlv.TypeLengthValue;
import io.snice.codecs.codec.gtp.gtpc.v1.ie.tlv.TypeLengthValueFramer;
import io.snice.codecs.codec.gtp.gtpc.v1.ie.tv.TypeValue;
import io.snice.codecs.codec.gtp.gtpc.v1.ie.tv.TypeValueFramer;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.TypeLengthInstanceValue;
import io.snice.codecs.codec.gtp.type.GtpType;

/**
 * In GTPv1 there are two kinds of Information Elements (IEs), The TV (Type, Value)
 * and the TLV (Type, Length, Value). Which type the next IE is, is denoted by the most
 * significant bit of the Type-field where 0 is TV and 1 is TLV.
 */
public interface InfoElement<T extends GtpType> {

    /**
     * GTPv1 Type Value information element. Those are the elements that are of fixed length
     * and as such, pre-defined in the specification.
     *
     * @return
     */
    default boolean isTypeValue() {
        return false;
    }

    /**
     * GTPv1 Type Length Value information element. Those are the elements that are of variable length.
     *
     * @return true if this {@link InfoElement} is indeed a GTPv1 {@link TypeLengthValue}, false otherwise.
     */
    default boolean isTypeLengthValue() {
        return false;
    }

    /**
     * GTPv2 Type Length Instance Value information element. These are information elements that are
     * of variable length and since they can occur multiple times in a GTPv2 message, they also have
     * an instance type associated with them.
     *
     * @return
     */
    default boolean isTypeLengthInstanceValue() {
        return false;
    }


    byte getType();

    T getValue();

    default int getTypeAsDecimal() {
        return Byte.toUnsignedInt(getType());
    }

    <V extends InfoElement<T>> V ensure();

    /**
     * This is just the length of the actual value. Hence, it is not the {@link #getTotalSize()} as you would
     * write out ot the network.
     *
     * @return the length of the actual value in bytes.
     */
    int getLength();

    /**
     * This is the total length of the entire IE, including header + value. This is the total size
     * you will be writing to the network.
     *
     * @return the length of the actual entire {@link InfoElement} in bytes.
     */
    default int getTotalSize() {
        return getRaw().capacity();
    }

    /**
     * See TS 29.274 section 8.2 and section 6.1.3.
     *
     * In short, both the type and the instance number is used to identify
     * exactly what a particular IE means. This to disambiguate IE of the same
     * type from each other.
     *
     * TODO: move out of the generic one and into the actual {@link TypeLengthInstanceValue} one.
     *
     * @return the instance number of this TLIV (it's only 4 bits so values will only be between 0-15)
     */
    default int getInstance() {
        throw new RuntimeException("Should be moved to TLIV only");
    }

    /**
     * Get the full "raw" buffer, which represents the entire info element, including
     * any headers etc.
     *
     * @return
     */
    Buffer getRaw();

    default TypeLengthInstanceValue<T> toTliv() throws ClassCastException {
        throw new ClassCastException("Unable to cast a " + getClass().getName() + " into a " + TypeLengthInstanceValue.class.getName());
    }

    default TypeValue<T> toTypeValue() throws ClassCastException {
        throw new ClassCastException("Unable to cast a " + getClass().getName() + " into a " + TypeValue.class.getName());
    }

    default TypeLengthValue<T> toTypeLengthValue() throws ClassCastException {
        throw new ClassCastException("Unable to cast a " + getClass().getName() + " into a " + TypeLengthValue.class.getName());
    }

    static InfoElement<? extends GtpType> frame(final Buffer buffer) {
        Buffers.assertBufferCapacityAtLeast(buffer, 1);

        // Bit is set to 1, it's TLV, else TV
        if (buffer.getBit7(0)) {
            return TypeLengthValueFramer.frame(buffer);
        }

        return TypeValueFramer.frame(buffer);
    }


}
