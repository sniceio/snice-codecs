/**
 *
 */
package io.snice.codecs.codec.internet.ipv4;

import io.snice.buffer.Buffer;
import io.snice.codecs.codec.Protocol;
import io.snice.codecs.codec.internet.IpMessage;
import io.snice.codecs.codec.internet.ipv4.impl.IPv4PacketImpl;

import static io.snice.buffer.Buffers.assertBufferCapacityAtLeast;
import static io.snice.preconditions.PreConditions.assertArgument;

/**
 * Represents a message from the Network Layer (layer 3). Actually, to be
 * completely honest, the model implemented (at least so far) is more geared
 * towards what is commonly referred to as the Internet Layer and is strictly
 * speaking not quite the same as the Network Layer as specified by the OSI
 * model. However, until it becomes an issue this little "issue" is going to be
 * ignored and for now the Network Layer is equal to the Internet Layer.
 *
 * The current version of pkts.io is focused on IP anyway so...
 *
 * @author jonas@jonasborjesson.com
 */
public interface IPv4Message<T> extends IpMessage<T> {

    static IPv4Message<Buffer> frame(final Buffer buffer) {
        assertBufferCapacityAtLeast(buffer, 20, "Can't be an IPv4 packet. Too short");

        // the ipv4 headers are always 20 bytes unless
        // the length is greater than 5
        final Buffer headers = buffer.slice(20);

        // byte 1, contains the version and the length
        final byte b = headers.getByte(0);
        final int version = b >>> 4 & 0x0F;
        assertArgument(version == 4, "Wrong IP version. Should be 4, was " + version);
        final int headerLength = b & 0x0F;

        // byte 2 - 3: the total length, which includes the header length
        final int totalLength = headers.getUnsignedShort(2);

        // if the length is greater than 5 there are options available,
        // which we currently do not support.
        if (headerLength > 5) {
            throw new IllegalArgumentException("Currently do not handle IPv4 options");
        }

        assertBufferCapacityAtLeast(buffer, totalLength, "Not enough data in buffer. Expected at least " +
                totalLength + " but only " + buffer.capacity() + " was available");

        final int headerLengthBytes = headerLength * 4;
        final Buffer payload = buffer.slice(headerLengthBytes, totalLength);
        final Buffer ipv4Packet = buffer.slice(totalLength);

        final int options = 0; // because we are not handling it right now
        return new IPv4PacketImpl(ipv4Packet, headers, options, payload);
    }

    int getTTL();

    /**
     * Get the raw protocol from the IP header.
     *
     * @return the byte representing the encapsulated protocol. E.g., 0x06 is TCP
     * and 0x11 (decimal 17) is UDP
     */
    byte getRawProtocol();

    Protocol getProtocol();

    Buffer getDestinationIp();

    String getDestinationIpAsString();

    Buffer getSourceIp();

    String getSourceIpAsString();

    /**
     * The checksum of the IP-packet. The checksum in an IP-packet is a 16 bit
     * checksum of the header bytes (which the checksum set to zero) and is
     * returned as a unsigned short (hence an int)
     *
     * Checkout
     *
     * @return
     */
    int getIpChecksum();

    /**
     * After you change anything in an IP packet (apart from the payload) you
     * should re-calculate the checksum. If you don't, if this then is written
     * to a pcap and later opened in e.g. wireshark, then all packets will be
     * flagged as bad checksums.
     *
     * TODO: needs to be done as part of a builder. the IPv4 is immutable so
     * can't change it...
     */
    void reCalculateChecksum();

    boolean verifyIpChecksum();

    /**
     *
     * @return
     */
    boolean isFragmented();

    /**
     * The Reserved flag is part of the three-bit flag field and those flags
     * are: (in order, from high order to low order):
     *
     * <pre>
     * bit 0: Reserved; must be zero.
     * bit 1: Don't Fragment (DF)
     * bit 2: More Fragments (MF)
     * </pre>
     *
     * (source http://en.wikipedia.org/wiki/IPv4)
     *
     * @return should always return false
     */
    boolean isReservedFlagSet();

    /**
     * The DF flag is part of the three-bit flag field and those flags are: (in
     * order, from high order to low order):
     *
     * <pre>
     * bit 0: Reserved; must be zero.
     * bit 1: Don't Fragment (DF)
     * bit 2: More Fragments (MF)
     * </pre>
     *
     * If the DF flag is set, and fragmentation is required to route the packet,
     * then the packet is dropped. This can be used when sending packets to a
     * host that does not have sufficient resources to handle fragmentation. It
     * can also be used for Path MTU Discovery, either automatically by the host
     * IP software, or manually using diagnostic tools such as ping or
     * traceroute. For unfragmented packets, the MF flag is cleared. For
     * fragmented packets, all fragments except the last have the MF flag set.
     * The last fragment has a non-zero Fragment Offset field, differentiating
     * it from an unfragmented packet.
     *
     * (source http://en.wikipedia.org/wiki/IPv4)
     *
     * @return
     */
    boolean isDontFragmentSet();

    /**
     * The MF flag is part of the three-bit flag field and those flags are: (in
     * order, from high order to low order):
     *
     * <pre>
     * bit 0: Reserved; must be zero.
     * bit 1: Don't Fragment (DF)
     * bit 2: More Fragments (MF)
     * </pre>
     *
     * If the DF flag is set, and fragmentation is required to route the packet,
     * then the packet is dropped. This can be used when sending packets to a
     * host that does not have sufficient resources to handle fragmentation. It
     * can also be used for Path MTU Discovery, either automatically by the host
     * IP software, or manually using diagnostic tools such as ping or
     * traceroute. For unfragmented packets, the MF flag is cleared. For
     * fragmented packets, all fragments except the last have the MF flag set.
     * The last fragment has a non-zero Fragment Offset field, differentiating
     * it from an unfragmented packet.
     *
     * (source http://en.wikipedia.org/wiki/IPv4)
     *
     * @return
     */
    boolean isMoreFragmentsSet();

    /**
     * The fragment offset field, measured in units of eight-byte blocks, is 13
     * bits long and specifies the offset of a particular fragment relative to
     * the beginning of the original unfragmented IP datagram. The first
     * fragment has an offset of zero. This allows a maximum offset of (213 – 1)
     * × 8 = 65,528 bytes, which would exceed the maximum IP packet length of
     * 65,535 bytes with the header length included (65,528 + 20 = 65,548
     * bytes).
     *
     * (source http://en.wikipedia.org/wiki/IPv4)
     *
     * @return
     */
    short getFragmentOffset();

    /**
     * The total length, including header and data. Since the IPv4 header is at least 20 bytes, the total
     * length will at least by 20 bytes.
     * @return
     */
    int getTotalLength();

    /**
     * Get the payload length.
     */
    int getPayloadLength();

    /**
     * Get the raw payload only (so excluding headers);
     */
    T getPayload();

    @Override
    default IPv4Message<T> toIPv4() {
        return this;
    }

    @Override
    default boolean isIPv4() {
        return true;
    }

    @Override
    default boolean isUDP() {
        return getRawProtocol() == (byte) 0x11;
    }

    @Override
    default boolean isTCP() {
        return getRawProtocol() == (byte) 0x06;
    }

    @Override
    default boolean isSCTP() {
        return getRawProtocol() == (byte) 0x84;
    }

}
