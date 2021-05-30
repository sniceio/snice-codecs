package io.snice.codecs.codec.gtp;

import io.snice.buffer.Buffer;
import io.snice.codecs.codec.gtp.gtpc.InfoElement;
import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1Message;
import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1Request;
import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1Response;
import io.snice.codecs.codec.gtp.gtpc.v1.impl.Utils;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Message;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2MessageType;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Request;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Response;
import io.snice.codecs.codec.gtp.gtpc.v2.messages.tunnel.CreateSessionRequest;
import io.snice.codecs.codec.gtp.gtpc.v2.messages.tunnel.DeleteSessionRequest;

import java.util.List;
import java.util.Optional;

import static io.snice.preconditions.PreConditions.assertNotNull;

/**
 *
 */
public interface GtpMessage {

    static GtpMessage frame(final Buffer buffer) throws GtpParseException, IllegalArgumentException {
        assertNotNull(buffer, "The buffer cannot be null");
        if (GtpHeader.getGtpVersion(buffer) == 1) {
            return Gtp1Message.frame(buffer);
        }

        return Gtp2Message.frame(buffer);
    }

    /**
     * Get the entire {@link GtpMessage} as a {@link Buffer}, which you then can use
     * to e.g. write to network socket.
     */
    default Buffer getBuffer() {
        throw new RuntimeException("Not yet implemented");
    }

    default boolean isRequest() {
        return false;
    }

    default boolean isResponse() {
        return false;
    }

    default boolean isGtpVersion1() {
        return getVersion() == 1;
    }

    default boolean isGtpVersion2() {
        return getVersion() == 2;
    }


    default Gtp1Message toGtp1Message() {
        throw new ClassCastException("Unable to cast a " + getClass().getName() + " into a " + Gtp1Message.class.getName());
    }

    default Gtp1Request toGtp1Request() {
        throw new ClassCastException("Unable to cast a " + getClass().getName() + " into a " + Gtp1Request.class.getName());
    }

    default Gtp1Response toGtp1Response() {
        throw new ClassCastException("Unable to cast a " + getClass().getName() + " into a " + Gtp1Response.class.getName());
    }

    default Gtp2Message toGtp2Message() {
        throw new ClassCastException("Unable to cast a " + getClass().getName() + " into a " + Gtp2Message.class.getName());
    }

    default Gtp2Request toGtp2Request() {
        throw new ClassCastException("Unable to cast a " + getClass().getName() + " into a " + Gtp2Request.class.getName());
    }

    default CreateSessionRequest toCreateSessionRequest() {
        throw new ClassCastException("Unable to cast a " + getClass().getName() + " into a " + CreateSessionRequest.class.getName());
    }

    default DeleteSessionRequest toDeleteSessionRequest() {
        throw new ClassCastException("Unable to cast a " + getClass().getName() + " into a " + DeleteSessionRequest.class.getName());
    }

    default Gtp2Response toGtp2Response() {
        throw new ClassCastException("Unable to cast a " + getClass().getName() + " into a " + Gtp2Response.class.getName());
    }

    default boolean isEchoRequest() {
        // NOTE: this works because the the type value for echo is the same in
        //       both GTV1 and GTPv2.
        return getMessageTypeDecimal() == Gtp2MessageType.ECHO_REQUEST.getType();
    }

    default boolean isEchoResponse() {
        return getMessageTypeDecimal() == Gtp2MessageType.ECHO_RESPONSE.getType();
    }

    <T extends GtpHeader> T getHeader();

    /**
     * TODO: this is wrong because the base class for GTPv1 and GTPv2 information elements are
     * different. Perhaps I should change that instead.
     *
     * @return
     */
    List<? extends InfoElement> getInfoElements();

    /**
     * Find the first {@link InfoElement} bases on its type value.
     */
    default Optional<? extends InfoElement> getInfoElement(final byte type) {
        return Utils.getInformationElement(type, getInfoElements());
    }

    default int getMessageTypeDecimal() {
        return getHeader().getMessageTypeDecimal();
    }

    default int getVersion() {
        return getHeader().getVersion();
    }

}
