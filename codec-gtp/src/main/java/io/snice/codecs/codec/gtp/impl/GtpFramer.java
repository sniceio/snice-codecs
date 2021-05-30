package io.snice.codecs.codec.gtp.impl;

import io.snice.buffer.Buffer;
import io.snice.buffer.ReadableBuffer;
import io.snice.codecs.codec.gtp.GtpHeader;
import io.snice.codecs.codec.gtp.GtpMessage;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.Imsi;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.TypeLengthInstanceValue;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.impl.RawTypeLengthInstanceValue;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2InfoElement;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static io.snice.preconditions.PreConditions.assertNotNull;

/**
 * Base class for all things related to framing GTP messages.
 *
 * @author jonas@jonasborjesson.com
 */
public final class GtpFramer {

    public static GtpMessage frameGtpMessage(final Buffer buffer) {
        assertNotNull(buffer, "The buffer cannot be null");
        final ReadableBuffer readable = buffer.toReadableBuffer();
        final GtpHeader header = frameGtpHeader(readable);
        return null;
    }

    public static GtpHeader frameGtpHeader(final Buffer buffer) {
        assertNotNull(buffer, "The buffer cannot be null");
        return frameGtpHeader(buffer.toReadableBuffer());
    }

}
