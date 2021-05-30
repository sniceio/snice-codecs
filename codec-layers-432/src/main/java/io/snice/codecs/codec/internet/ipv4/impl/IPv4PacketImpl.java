/**
 *
 */
package io.snice.codecs.codec.internet.ipv4.impl;

import io.snice.buffer.Buffer;
import io.snice.codecs.codec.Protocol;
import io.snice.codecs.codec.internet.ipv4.IPv4Message;

/**
 * @author jonas@jonasborjesson.com
 */
public final class IPv4PacketImpl<T> implements IPv4Message<T> {

    private final Buffer buffer;
    private final Buffer headers;
    private final T payload;
    private final int options;

    /**
     *
     */
    public IPv4PacketImpl(final Buffer buffer, final Buffer headers, final int options, final T payload) {
        this.buffer = buffer;
        this.headers = headers;
        this.payload = payload;
        this.options = options;
    }

    @Override
    public Buffer getBuffer() {
        return buffer;
    }

    @Override
    public int getTTL() {
        return buffer.getByte(8);
    }

    @Override
    public byte getRawProtocol() {
        return headers.getByte(9);
    }

    @Override
    public Protocol getProtocol() {
        return Protocol.valueOf(getRawProtocol());
    }

    @Override
    public Buffer getDestinationIp() {
        return headers.slice(16, 20);
    }

    @Override
    public String getDestinationIpAsString() {
        return headers.toIPv4String(16);
    }

    @Override
    public Buffer getSourceIp() {
        return headers.slice(12, 16);
    }

    @Override
    public String getSourceIpAsString() {
        return headers.toIPv4String(12);
    }


    @Override
    public int getIpChecksum() {
        return this.headers.getUnsignedShort(10);
    }

    /**
     * Algorithm adopted from RFC 1071 - Computing the Internet Checksum
     *
     * @return
     */
    private int calculateChecksum() {
        long sum = 0;
        for (int i = 0; i < this.headers.capacity() - 1; i += 2) {
            if (i != 10) {
                sum += this.headers.getUnsignedShort(i);
            }
        }

        while (sum >> 16 != 0) {
            sum = (sum & 0xffff) + (sum >> 16);
        }

        return (int) ~sum & 0xFFFF;
    }

    /**
     * Get the raw source ip as 32-bit integer
     *
     * Note, these are the raw bits and should be treated as such. If you really
     * want to print it, then you should treat it as unsigned
     *
     * @return
     */
    public int getRawSourceIpInt() {
        return this.headers.getInt(12);
    }

    /**
     * Get the raw destination ip as a 32-bit integer.
     *
     * Note, these are the raw bits and should be treated as such. If you really
     * want to print it, then you should treat it as unsigned
     *
     * @return
     */
    public int getRawDestinationIpInt() {
        return this.headers.getInt(16);
    }


    @Override
    public int getTotalLength() {
        // byte 2 - 3
        return this.headers.getUnsignedShort(2);
    }

    @Override
    public int getPayloadLength() {
        return getTotalLength() - headers.capacity();
    }

    @Override
    public T getPayload() {
        return payload;
    }

    /**
     * Whenever we change a value in the IP packet we need to update the
     * checksum as well.
     */
    @Override
    public void reCalculateChecksum() {
        final int checksum = calculateChecksum();
        // this.headers.setUnsignedShort(10, checksum);
    }

    @Override
    public boolean verifyIpChecksum() {
        return calculateChecksum() == getIpChecksum();
    }


    /**
     * The version of this ip frame, will always be 4
     *
     * @return
     */
    public int getVersion() {
        return 4;
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public boolean isFragmented() {
        return isMoreFragmentsSet() || getFragmentOffset() > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isReservedFlagSet() {
        final byte b = this.headers.getByte(6);
        return (b & 0x80) == 0x80;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDontFragmentSet() {
        final byte b = this.headers.getByte(6);
        return (b & 0x40) == 0x40;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMoreFragmentsSet() {
        final byte b = this.headers.getByte(6);
        return (b & 0x20) == 0x20;
    }

    @Override
    public short getFragmentOffset() {
        final byte a = this.headers.getByte(6);
        final byte b = this.headers.getByte(7);
        return (short) ((a & 0x1F) << 8 | b & 0xFF);
    }

    public int getIdentification() {
        return this.headers.getUnsignedShort(4);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("IPv4 ");
        sb.append(" Total Length: ").append(getTotalLength())
                .append(" ID: ").append(getIdentification())
                .append(" DF: ").append(isDontFragmentSet() ? "Set" : "Not Set")
                .append(" MF: ").append(isMoreFragmentsSet() ? "Set" : "Not Set")
                .append(" Fragment Offset: ").append(getFragmentOffset());

        return sb.toString();
    }
}
