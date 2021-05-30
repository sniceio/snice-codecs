package io.snice.codecs.codec.transport.impl;

import io.snice.buffer.Buffer;
import io.snice.buffer.WritableBuffer;
import io.snice.buffer.impl.EmptyBuffer;
import io.snice.codecs.codec.Protocol;
import io.snice.codecs.codec.internet.ipv4.IPv4Builder;
import io.snice.codecs.codec.internet.ipv4.IPv4Message;
import io.snice.codecs.codec.internet.ipv4.impl.IPv4PacketImpl;
import io.snice.codecs.codec.transport.IPv4UdpBuilder;
import io.snice.codecs.codec.transport.UdpMessage;

import static io.snice.buffer.Buffers.assertBufferCapacityAtLeast;

public class UdpMessageImpl implements UdpMessage {

    public static IPv4UdpBuilder createUdpIPv4(final Buffer payload) {
        return new Builder(payload == null ? EmptyBuffer.EMPTY : payload);
    }

    public static UdpMessage frame(final Buffer buffer) {
        assertBufferCapacityAtLeast(buffer, UDP_HEADER_LENGTH, "The smallest UDP message is 8 bytes, this buffer contains less");
        final int length = buffer.getUnsignedShort(4);

        assertBufferCapacityAtLeast(buffer, length, "The total length of the UDP packet is " + length +
                " but buffer only contains " + buffer.capacity() + " bytes.");
        return new UdpMessageImpl(buffer.slice(0, length));
    }

    @Override
    public Buffer getBuffer() {
        return buffer;
    }

    @Override
    public Buffer getPayload() {
        return buffer.slice(UDP_HEADER_LENGTH, UDP_HEADER_LENGTH + getPayloadLength());
    }

    @Override
    public int getPayloadLength() {
        return buffer.getUnsignedShort(4) - UDP_HEADER_LENGTH;
    }

    @Override
    public int getDestinationPort() {
        return buffer.getUnsignedShort(2);
    }

    @Override
    public int getSourcePort() {
        return buffer.getUnsignedShort(0);
    }

    @Override
    public int getChecksum() {
        return buffer.getUnsignedShort(6);
    }

    private final Buffer buffer;

    private UdpMessageImpl(final Buffer buffer) {
        this.buffer = buffer;
    }

    private static class Builder extends IPv4Builder.BaseBuilder<UdpMessage> implements IPv4UdpBuilder {

        private final Buffer payload;

        private Builder(final Buffer payload) {
            // 20 for the IPv4 headers
            // 8 for UDP headers
            super(WritableBuffer.of(IPV4_HEADER_LENGTH + UDP_HEADER_LENGTH + payload.capacity()));

            buffer.setWriterIndex(IPV4_HEADER_LENGTH + UDP_HEADER_LENGTH);
            payload.writeTo(buffer);

            this.payload = payload;
        }

        @Override
        public IPv4UdpBuilder withSourcePort(final int port) {
            buffer.setUnsignedShort(IPV4_HEADER_LENGTH + 0, port);
            return this;
        }

        @Override
        public IPv4UdpBuilder withDestinationPort(final int port) {
            buffer.setUnsignedShort(IPV4_HEADER_LENGTH + 2, port);
            return this;
        }


        @Override
        public IPv4Message<UdpMessage> build() {
            // Set the protocol of the IPv4 packet
            buffer.setByte(9, Protocol.valueOf(Protocol.UDP));

            // total length of the IPV4 packet...
            buffer.setUnsignedShort(2, IPV4_HEADER_LENGTH + UDP_HEADER_LENGTH + payload.capacity());

            // set the Internet Header Length, which currently will always be 5 for us (it's the number of
            // 32 bit words) since we don't support options right now (which exists if IHL > 5)
            buffer.setBit0(0, true);
            buffer.setBit2(0, true);

            // total length of the UDP packet.
            buffer.setUnsignedShort(IPV4_HEADER_LENGTH + 4, UDP_HEADER_LENGTH + payload.capacity());

            final var ipv4UdpBuffer = buffer.build();

            final var ipv4Headers = ipv4UdpBuffer.slice(0, IPV4_HEADER_LENGTH);
            final var udpBuffer = ipv4UdpBuffer.slice(IPV4_HEADER_LENGTH, ipv4UdpBuffer.capacity());

            final var udp = new UdpMessageImpl(udpBuffer);
            return new IPv4PacketImpl<>(ipv4UdpBuffer, ipv4Headers, 0, udp);
        }
    }
}
