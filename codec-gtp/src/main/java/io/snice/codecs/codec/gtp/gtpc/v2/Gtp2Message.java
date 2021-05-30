package io.snice.codecs.codec.gtp.gtpc.v2;

import io.snice.buffer.Buffer;
import io.snice.codecs.codec.gtp.GtpMessage;
import io.snice.codecs.codec.gtp.gtpc.v2.Impl.Gtp2MessageBuilder;
import io.snice.codecs.codec.gtp.gtpc.v2.Impl.ImmutableGtp2Message;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.BearerContext;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.Cause;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.Imsi;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.Msisdn;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.TypeLengthInstanceValue;

import java.util.Optional;

public interface Gtp2Message extends GtpMessage {

    /**
     * Frame a given {@link Buffer} into a {@link Gtp2Message}.
     *
     * @param buffer the FULL buffer that is expected to also include the header.
     */
    static Gtp2Message frame(final Buffer buffer) {
        return ImmutableGtp2Message.frame(buffer);
    }

    static <T extends Gtp2Message> Gtp2MessageBuilder<T> create(final Gtp2MessageType type) {

        return ImmutableGtp2Message.create(type);
    }

    static <T extends Gtp2Message> Gtp2MessageBuilder<T> create(final Gtp2Header header) {
        return ImmutableGtp2Message.create(header);
    }

    @Override
    Gtp2Header getHeader();

    Gtp2MessageType getType();

    /**
     * Find the FIRST {@link TypeLengthInstanceValue} of the given type, IGNORING any instance numbers.
     * <p>
     * I.e., this method is NOT, I repeat, NOT, a convenience method for
     * {@link #getInfoElement(Gtp2InfoElement, int)} where the instance is set to zero.
     *
     * @param type
     * @return
     */
    Optional<? extends TypeLengthInstanceValue> getInfoElement(final Gtp2InfoElement type);

    Optional<? extends TypeLengthInstanceValue> getInfoElement(final Gtp2InfoElement type, final int instance);

    default Optional<Msisdn> getMsisdn() {
        return getInfoElement(Msisdn.TYPE).map(v -> (Msisdn) v.ensure());
    }

    default Optional<Imsi> getImsi() {
        return getInfoElement(Imsi.TYPE).map(v -> (Imsi) v.ensure());
    }

    default Optional<Cause> getCause() {
        return getInfoElement(Cause.TYPE).map(v -> (Cause) v.ensure());
    }

    /**
     * Note that there may be many {@link BearerContext}s in a message, this will only fetch
     * the first one and also the one with instance no zero.
     */
    default Optional<BearerContext> getBearerContext() {
        return getInfoElement(BearerContext.TYPE).map(v -> (BearerContext) v.ensure());
    }

    /**
     * Convenience method for checking if this message is a Create Session Request or not.
     *
     * @return
     */
    default boolean isCreateSessionRequest() {
        return getType() == Gtp2MessageType.CREATE_SESSION_REQUEST;
    }

    default boolean isCreateSessionResponse() {
        return getType() == Gtp2MessageType.CREATE_SESSION_RESPONSE;
    }


    default boolean isDeleteSessionRequest() {
        return getType() == Gtp2MessageType.DELETE_SESSION_REQUEST;
    }

    default boolean isDeleteSessionResponse() {
        return getType() == Gtp2MessageType.DELETE_SESSION_RESPONSE;
    }

    @Override
    default boolean isEchoRequest() {
        return getType() == Gtp2MessageType.ECHO_REQUEST;
    }

    @Override
    default boolean isEchoResponse() {
        return getType() == Gtp2MessageType.ECHO_RESPONSE;
    }

    @Override
    default Gtp2Message toGtp2Message() {
        return this;
    }

}
