package io.snice.codecs.codec.gtp.gtpc.v2.Impl;

import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2InfoElement;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.TypeLengthInstanceValue;

import java.util.List;
import java.util.Optional;

import static io.snice.preconditions.PreConditions.assertNotNull;

public class Utils {
    private Utils() {
        // No instantiations allowed, it's just a set of static utility functions
    }

    public static Optional<? extends TypeLengthInstanceValue> getInformationElement(final Gtp2InfoElement type,
                                                                                    final List<? extends TypeLengthInstanceValue> values) {
        assertNotNull(type, "The GTPv2 Information Element type cannot be null");
        return values.stream().filter(tliv -> type.getType() == tliv.getType()).findFirst();
    }

    public static Optional<? extends TypeLengthInstanceValue> getInformationElement(final Gtp2InfoElement type, final int instance,
                                                                                    final List<? extends TypeLengthInstanceValue> values) {
        assertNotNull(type, "The GTPv2 Information Element type cannot be null");
        return values.stream().filter(tliv -> type.getType() == tliv.getType() && tliv.getInstance() == instance).findFirst();
    }
}
