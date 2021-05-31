package io.snice.codecs.codec.gtp.gtpc.v1.ie.tlv;

import io.snice.codecs.codec.gtp.gtpc.InfoElement;
import io.snice.codecs.codec.gtp.type.GtpType;

public interface TypeLengthValue<T extends GtpType> extends InfoElement<T> {

    @Override
    default TypeLengthValue<T> toTypeLengthValue() throws ClassCastException {
        return this;
    }
}
