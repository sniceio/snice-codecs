package io.snice.codecs.codec.gtp.gtpc.v1.ie.tv;

import io.snice.codecs.codec.gtp.gtpc.InfoElement;
import io.snice.codecs.codec.gtp.type.GtpType;

public interface TypeValue<T extends GtpType> extends InfoElement<T> {

    default TypeValue<T> toTypeValue() throws ClassCastException {
        return this;
    }

}
