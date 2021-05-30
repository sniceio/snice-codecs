package io.snice.codecs.codec.gtp.gtpc.v1.impl;

import io.snice.buffer.Buffer;
import io.snice.codecs.codec.gtp.GtpParseException;
import io.snice.codecs.codec.gtp.UnknownGtp1MessageTypeException;
import io.snice.codecs.codec.gtp.gtpc.InfoElement;
import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1Header;
import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1Message;
import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1MessageType;
import io.snice.codecs.codec.gtp.gtpc.v1.messages.path.EchoRequest;
import io.snice.codecs.codec.gtp.gtpc.v1.messages.path.EchoResponse;

import java.util.List;
import java.util.Optional;

import static io.snice.preconditions.PreConditions.assertNotNull;

public class ImmutableGtp1Message implements Gtp1Message {

    private static <T extends Gtp1Message> Gtp1MessageBuilder<T> ensureBuilder(final Gtp1MessageType type) {
        return new GenericGtp1MessageBuilder<>(type);
    }

    private final Gtp1Header header;

    /**
     * The entire already encoded message, ready to be written to the network.
     * Since this is an immutable class, once it has been constructed, it cannot
     * be changed and as such, it is cheap and safe to externalize this object
     * over and over since all we are doing is dumping the same buffer.
     */
    private final Buffer buffer;

    /**
     * All the {@link InfoElement}s that is part of this message.
     */
    private final List<InfoElement> ies;

    /**
     * Optional payload. This is only valid for G-PDU message types.
     */
    private final Optional<Buffer> payload;

    private final Gtp1MessageType type;

    /**
     * Find an appropriate framer for the given type. If there is no specific one for the given
     * {@link Gtp1MessageType} we will be using the {@link GenericGtp1MessageFramer}.
     *
     * @param type   the message type.
     * @param header the already parsed header.
     * @param buffer the FULL buffer, which includes both the header and the body. Essentially,
     *               this framer will be used when we have read an entire message
     *               off of the network and as such, already have the entire byte-array representing
     *               the message.
     * @return
     */
    private static <T extends Gtp1Message> Gtp1MessageFramer<T> ensureFramer(final Gtp1MessageType type,
                                                                             final Gtp1Header header,
                                                                             final Buffer buffer) {
        // TODO: all of this can and should be auto generated!
        switch (type) {
            case ECHO_REQUEST:
                return (Gtp1MessageFramer<T>) EchoRequest.from(type, header, buffer);
            case ECHO_RESPONSE:
                return (Gtp1MessageFramer<T>) EchoResponse.from(type, header, buffer);
            default:
                return new GenericGtp1MessageFramer<>(type, header, buffer);
        }

    }

    public static Gtp1Message frame(final Buffer buffer) {
        assertNotNull(buffer, "The buffer cannot be null");
        final Gtp1Header header = Gtp1Header.frame(buffer);

        if (buffer.capacity() < header.getTotalLength()) {
            throw new GtpParseException(0, "Not enough bytes in the given buffer for " +
                    "the entire GTPv1 Message to be framed. Need a total of " + header.getTotalLength() +
                    " bytes but buffer only contained " + buffer.capacity() + " bytes");
        }

        try {
            final Gtp1MessageType type = Gtp1MessageType.lookup(header.getMessageTypeDecimal());
            if (type == null) {
                throw new UnknownGtp1MessageTypeException(header.getMessageTypeDecimal());
            }

            final Gtp1MessageFramer<?> framer = ensureFramer(type, header, buffer);
            return framer.build();
        } catch (final IndexOutOfBoundsException e) {
            throw new GtpParseException(0, "Not enough bytes in the buffer. The length of the body is " +
                    header.getBodyLength() + " but there was only " + (buffer.capacity() - header.getHeaderLength()) +
                    " bytes available");
        }
    }

    public static <T extends Gtp1Message> Gtp1MessageBuilder<T> create(final Gtp1MessageType type) {
        return ensureBuilder(type);
    }

    protected ImmutableGtp1Message(final Gtp1MessageType type, final Gtp1Header header,
                                   final Buffer buffer,
                                   final List<InfoElement> ies,
                                   final Buffer payload) {
        this.header = header;
        this.buffer = buffer;
        this.ies = ies;
        this.type = type;
        this.payload = Optional.ofNullable(payload);
    }

    @Override
    public Gtp1MessageType getType() {
        return type;
    }

    @Override
    public Optional<Buffer> getPayload() {
        return payload;
    }

    @Override
    public Optional<? extends InfoElement> getInfoElement(final byte type) {
        return Utils.getInformationElement(type, ies);
    }

    @Override
    public Buffer getBuffer() {
        return buffer;
    }

    @Override
    public List<? extends InfoElement> getInfoElements() {
        return ies;
    }

    @Override
    public Gtp1Header getHeader() {
        return header;
    }

}
