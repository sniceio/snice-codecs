package io.snice.codecs.codec.gtp.gtpc.v1;

import io.snice.buffer.Buffer;
import io.snice.buffer.ReadableBuffer;
import io.snice.codecs.codec.gtp.GtpHeader;
import io.snice.codecs.codec.gtp.GtpParseException;
import io.snice.codecs.codec.gtp.GtpVersionException;
import io.snice.codecs.codec.gtp.Teid;
import io.snice.codecs.codec.gtp.gtpc.v1.impl.ImmutableGtp1Header;

import java.util.Optional;

import static io.snice.preconditions.PreConditions.assertNotNull;

public interface Gtp1Header extends GtpHeader {

    static Gtp1HeaderBuilder of(final Gtp1MessageType type) {
        return ImmutableGtp1Header.of(type);
    }

    static Gtp1Header frame(final Buffer buffer) throws IllegalArgumentException, GtpParseException, GtpVersionException {
        assertNotNull(buffer, "The buffer cannot be null");
        return frame(buffer.toReadableBuffer());
    }

    static Gtp1Header frame(final ReadableBuffer buffer) throws IllegalArgumentException, GtpParseException, GtpVersionException {
        return ImmutableGtp1Header.frame(buffer);
    }

    Teid getTeid();

    Gtp1MessageType getType();

    /**
     * Copy the current {@link Gtp1Header} and return a builder so it can be modified.
     */
    Gtp1HeaderBuilder copy();


    @Override
    default int getVersion() {
        return 1;
    }

    @Override
    default Gtp1Header toGtp1Header() throws ClassCastException {
        return this;
    }

    /**
     * In GTPv1, the sequence no is an optional parameter, whereas in GTPv2 it is a mandatory
     * parameter and also 1 byte longer.
     */
    Optional<Buffer> getSequenceNo();

    Optional<Integer> getSequenceNoAsDecimal();
}
