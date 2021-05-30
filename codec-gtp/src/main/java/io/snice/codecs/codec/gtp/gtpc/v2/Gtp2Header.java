package io.snice.codecs.codec.gtp.gtpc.v2;

import io.snice.buffer.Buffer;
import io.snice.buffer.ReadableBuffer;
import io.snice.codecs.codec.gtp.GtpHeader;
import io.snice.codecs.codec.gtp.GtpParseException;
import io.snice.codecs.codec.gtp.GtpVersionException;
import io.snice.codecs.codec.gtp.Teid;
import io.snice.codecs.codec.gtp.gtpc.v2.Impl.ImmutableGtp2Header;

import java.util.Optional;

import static io.snice.preconditions.PreConditions.assertNotNull;

public interface Gtp2Header extends GtpHeader {

    static Gtp2Header frame(final Buffer buffer) throws IllegalArgumentException, GtpParseException, GtpVersionException {
        assertNotNull(buffer, "The buffer cannot be null");
        return frame(buffer.toReadableBuffer());
    }

    static Gtp2Header frame(final ReadableBuffer buffer) throws IllegalArgumentException, GtpParseException, GtpVersionException {
        return ImmutableGtp2Header.frame(buffer);
    }

    static Gtp2HeaderBuilder of(final Gtp2MessageType type) {
        return ImmutableGtp2Header.of(type);
    }

    Gtp2MessageType getType();

    /**
     * Copy the current {@link Gtp2Header} and return a builder so it can be modified.
     */
    Gtp2HeaderBuilder copy();


    /**
     * The tunnel endpoint identifier is optional in GTPv2.
     */
    Optional<Teid> getTeid();

    @Override
    default int getVersion() {
        return 2;
    }

    @Override
    default Gtp2Header toGtp2Header() throws ClassCastException {
        return this;
    }

    /**
     * In GTPv2, the sequence no is mandatory, unlike GTPv1 where it is an optional parameter.
     */
    Buffer getSequenceNo();

    int getSequenceNoAsDecimal();
}
