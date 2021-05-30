package io.snice.codecs.codec.gtp.gtpc.v1;

import io.snice.buffer.Buffer;
import io.snice.buffer.ReadableBuffer;
import io.snice.codecs.codec.gtp.GtpMessage;
import io.snice.codecs.codec.gtp.gtpc.v1.ie.tv.Imsi;
import io.snice.codecs.codec.gtp.gtpc.v1.impl.Gtp1MessageBuilder;
import io.snice.codecs.codec.gtp.gtpc.v1.impl.ImmutableGtp1Message;

import java.util.Optional;

public interface Gtp1Message extends GtpMessage {

    static Gtp1Message frame(final Buffer buffer) {
        return ImmutableGtp1Message.frame(buffer);
    }

    static Gtp1Message frame(final Gtp1Header header, final ReadableBuffer buffer) {
        throw new RuntimeException("Not yet implemented");
    }

    static <T extends Gtp1Message> Gtp1MessageBuilder<T> create(final Gtp1MessageType type) {
        return ImmutableGtp1Message.create(type);
    }

    @Override
    default Gtp1Message toGtp1Message() {
        return this;
    }

    Gtp1MessageType getType();

    default Optional<Imsi> getImsi() {
        return getInfoElement(Imsi.TYPE.getType()).map(v -> (Imsi) v.ensure());
    }

    /**
     * If this a PDU message, it is expected that there is a payload available.
     */
    Optional<Buffer> getPayload();

}
