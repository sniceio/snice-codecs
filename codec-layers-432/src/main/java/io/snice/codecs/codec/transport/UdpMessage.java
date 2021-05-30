package io.snice.codecs.codec.transport;

import io.snice.buffer.Buffer;
import io.snice.buffer.Buffers;
import io.snice.buffer.impl.EmptyBuffer;
import io.snice.codecs.codec.transport.impl.UdpMessageImpl;

public interface UdpMessage {

    /**
     * The length of a UDP header.
     */
    int UDP_HEADER_LENGTH = 8;

    /**
     * Create a new {@link UdpMessage} over IPv4 with the given payload as, well, payload.
     *
     * @param payload the raw payload of the UDP packet, which is allowed to be empty (so null or the
     *                empty buffer)
     */
    static IPv4UdpBuilder createUdpIPv4(final Buffer payload) {
        return UdpMessageImpl.createUdpIPv4(payload);
    }

    static IPv4UdpBuilder createUdpIPv4(final String payload) {
        final var buffer = payload == null ? EmptyBuffer.EMPTY : Buffers.wrap(payload);
        return UdpMessageImpl.createUdpIPv4(buffer);
    }

    static UdpMessage frame(final Buffer buffer) {
        return UdpMessageImpl.frame(buffer);
    }

    /**
     * Get the underlying {@link Buffer} that represents this {@link UdpMessage}
     */
    Buffer getBuffer();

    /**
     * Get the payload of this {@link UdpMessage}.
     */
    Buffer getPayload();

    /**
     * Get the length of the payload of this {@link UdpMessage}
     */
    int getPayloadLength();

    int getDestinationPort();

    int getSourcePort();

    int getChecksum();

}
