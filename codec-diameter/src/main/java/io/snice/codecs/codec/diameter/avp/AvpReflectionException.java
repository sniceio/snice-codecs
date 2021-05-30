package io.snice.codecs.codec.diameter.avp;

import io.snice.codecs.codec.diameter.DiameterException;
import io.snice.codecs.codec.diameter.DiameterParseException;

public class AvpReflectionException extends DiameterException {

    public AvpReflectionException(final String message) {
        super(message);
    }

    public AvpReflectionException(final String message, final Exception cause) {
        super(message, cause);
    }
}
