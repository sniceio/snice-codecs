package io.snice.codecs.codec.gtp.gtpc.v1.impl;

import io.snice.codecs.codec.gtp.gtpc.InfoElement;

import java.util.List;
import java.util.Optional;

public class Utils {
    private Utils() {
        // No instantiations allowed, it's just a set of static utility functions
    }

    public static Optional<? extends InfoElement> getInformationElement(final byte type,
                                                                        final List<? extends InfoElement> values) {
        return values.stream().filter(ie -> ie.getType() == type).findFirst();
    }

}
