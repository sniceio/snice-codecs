package io.snice.codecs.codec.gtp.gtpc.v2.Impl;

import io.snice.buffer.Buffer;
import io.snice.codecs.codec.gtp.GtpParseException;
import io.snice.codecs.codec.gtp.UnknownGtp2MessageTypeException;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Header;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2InfoElement;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Message;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2MessageType;
import io.snice.codecs.codec.gtp.gtpc.v2.messages.path.EchoRequest;
import io.snice.codecs.codec.gtp.gtpc.v2.messages.path.EchoResponse;
import io.snice.codecs.codec.gtp.gtpc.v2.messages.tunnel.CreateSessionRequest;
import io.snice.codecs.codec.gtp.gtpc.v2.messages.tunnel.CreateSessionResponse;
import io.snice.codecs.codec.gtp.gtpc.v2.messages.tunnel.DeleteSessionRequest;
import io.snice.codecs.codec.gtp.gtpc.v2.messages.tunnel.DeleteSessionResponse;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.TypeLengthInstanceValue;

import java.util.List;
import java.util.Optional;

import static io.snice.preconditions.PreConditions.assertNotNull;

public class ImmutableGtp2Message implements Gtp2Message {


    /**
     * Find an appropriate framer for the given type. If there is no specific one for the given
     * {@link Gtp2MessageType} we will be using the {@link GenericGtp2MessageFramer}.
     *
     * @param type   the message type.
     * @param header the already parsed header.
     * @param buffer the FULL buffer, which includes both the header and the body. Essentially,
     *               this particular builder will be used when we have read an entire message
     *               off of the network and as such, already have the entire byte-array representing
     *               the message.
     * @return
     */
    private static <T extends Gtp2Message> Gtp2MessageFramer<T> ensureFramer(final Gtp2MessageType type,
                                                                             final Gtp2Header header,
                                                                             final Buffer buffer) {
        // TODO: all of this can and should be auto generated!
        switch (type) {
            case ECHO_REQUEST:
                return (Gtp2MessageFramer<T>) EchoRequest.from(type, header, buffer);
            case ECHO_RESPONSE:
                return (Gtp2MessageFramer<T>) EchoResponse.from(type, header, buffer);
            case CREATE_SESSION_REQUEST:
                return (Gtp2MessageFramer<T>) CreateSessionRequest.from(type, header, buffer);
            case CREATE_SESSION_RESPONSE:
                return (Gtp2MessageFramer<T>) CreateSessionResponse.from(type, header, buffer);
            case DELETE_SESSION_REQUEST:
                return (Gtp2MessageFramer<T>) DeleteSessionRequest.from(type, header, buffer);
            case DELETE_SESSION_RESPONSE:
                return (Gtp2MessageFramer<T>) DeleteSessionResponse.from(type, header, buffer);
            default:
                return new GenericGtp2MessageFramer<>(type, header, buffer);
        }

    }

    private static <T extends Gtp2Message> Gtp2MessageBuilder<T> ensureBuilder(final Gtp2MessageType type) {
        switch (type) {
            case ECHO_REQUEST:
                return (Gtp2MessageBuilder<T>) EchoRequest.create();
            case CREATE_SESSION_REQUEST:
                return (Gtp2MessageBuilder<T>) CreateSessionRequest.create();
            case CREATE_SESSION_RESPONSE:
                return (Gtp2MessageBuilder<T>) CreateSessionResponse.create();
            case DELETE_SESSION_REQUEST:
                return (Gtp2MessageBuilder<T>) DeleteSessionRequest.create();
            case DELETE_SESSION_RESPONSE:
                return (Gtp2MessageBuilder<T>) DeleteSessionResponse.create();
            default:
                return new GenericGtp2MessageBuilder<>(type);
        }
    }

    private static <T extends Gtp2Message> Gtp2MessageBuilder<T> ensureBuilder(final Gtp2Header header) {
        final var type = header.getType();
        switch (type) {
            case CREATE_SESSION_REQUEST:
                return (Gtp2MessageBuilder<T>) CreateSessionRequest.create(header);
            case CREATE_SESSION_RESPONSE:
                return (Gtp2MessageBuilder<T>) CreateSessionResponse.create(header);
            case DELETE_SESSION_REQUEST:
                return (Gtp2MessageBuilder<T>) DeleteSessionRequest.create(header);
            case DELETE_SESSION_RESPONSE:
                return (Gtp2MessageBuilder<T>) DeleteSessionResponse.create(header);
            default:
                return new GenericGtp2MessageBuilder<>(header);
        }
    }

    private final Gtp2Header header;

    /**
     * The entire already encoded message, ready to be written to the network.
     * Since this is an immutable class, once it has been constructed, it cannot
     * be changed and as such, it is cheap and safe to externalize this object
     * over and over since all we are doing is dumping the same buffer.
     */
    private final Buffer buffer;

    protected final List<TypeLengthInstanceValue> values;

    private final Gtp2MessageType type;

    public static Gtp2Message frame(final Buffer buffer) {
        assertNotNull(buffer, "The buffer cannot be null");
        final Gtp2Header header = Gtp2Header.frame(buffer);

        if (buffer.capacity() < header.getTotalLength()) {
            throw new GtpParseException(0, "Not enough bytes in the given buffer for " +
                    "the entire GTPv2 Message to be framed. Need a total of " + header.getTotalLength() +
                    " bytes but buffer only contained " + buffer.capacity() + " bytes");
        }

        try {
            final Gtp2MessageType type = Gtp2MessageType.lookup(header.getMessageTypeDecimal());
            if (type == null) {
                throw new UnknownGtp2MessageTypeException(header.getMessageTypeDecimal());
            }

            final Gtp2MessageFramer<?> framer = ensureFramer(type, header, buffer);
            return framer.build();
        } catch (final IndexOutOfBoundsException e) {
            e.printStackTrace();
            throw new GtpParseException(0, "Not enough bytes in the buffer. The length of the body is " +
                    header.getBodyLength() + " but there was only " + (buffer.capacity() - header.getHeaderLength()) +
                    " bytes available");
        }
    }

    public static <T extends Gtp2Message> Gtp2MessageBuilder<T> create(final Gtp2MessageType type) {
        return ensureBuilder(type);
    }

    public static <T extends Gtp2Message> Gtp2MessageBuilder<T> create(final Gtp2Header header) {
        return ensureBuilder(header);
    }

    protected ImmutableGtp2Message(final Gtp2MessageType type, final Gtp2Header header,
                                   final Buffer buffer, final List<TypeLengthInstanceValue> values) {
        this.header = header;
        this.buffer = buffer;
        this.values = values;
        this.type = type;
    }

    @Override
    public Gtp2MessageType getType() {
        return type;
    }

    @Override
    public Optional<? extends TypeLengthInstanceValue> getInfoElement(final Gtp2InfoElement type) {
        return Utils.getInformationElement(type, values);
    }

    @Override
    public Optional<? extends TypeLengthInstanceValue> getInfoElement(final Gtp2InfoElement type, final int instance) {
        return Utils.getInformationElement(type, instance, values);
    }

    @Override
    public Buffer getBuffer() {
        return buffer;
    }

    @Override
    public Gtp2Header getHeader() {
        return header;
    }

    @Override
    public List<? extends TypeLengthInstanceValue> getInfoElements() {
        return values;
    }

}
