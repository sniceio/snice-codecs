package io.snice.codecs.codec.internet;

import io.snice.buffer.Buffer;
import io.snice.codecs.codec.internet.ipv4.IPv4Message;
import io.snice.codecs.codec.internet.ipv6.IPv6Message;

import static io.snice.buffer.Buffers.assertBufferCapacityAtLeast;

public interface IpMessage<T> {


    static IpMessage<Buffer> frame(final Buffer buffer) {
        assertBufferCapacityAtLeast(buffer, 20, "Can't be an IPv4 or IPv6 packet. Too short");
        final int version = buffer.getByte(0) >>> 4 & 0x0F;
        switch (version) {
            case 4:
                return IPv4Message.frame(buffer);
            case 6:
                throw new RuntimeException("Sorry, haven't implemented IPv6 just yet");
            default:
                throw new RuntimeException("Unknown IP version (" + version + ")");
        }
    }

    /**
     * Get the underlying {@link Buffer} that represents this {@link IpMessage}
     */
    Buffer getBuffer();

    /**
     * Check whether or not the encapsulated transport protocol is UDP or not.
     */
    default boolean isUDP() {
        return false;
    }

    /**
     * Check whether or not the encapsulated transport protocol is TCP or not.
     */
    default boolean isTCP() {
        return false;
    }

    /**
     * Check whether or not the encapsulated transport protocol is SCTP or not.
     */
    default boolean isSCTP() {
        return false;
    }

    default IPv4Message<T> toIPv4() {
        throw new ClassCastException("Unable to cast a " + getClass().getName() + " into a " + IPv4Message.class.getName());
    }

    default boolean isIPv4() {
        return false;
    }

    default IPv6Message toIPv6() {
        throw new ClassCastException("Unable to cast a " + getClass().getName() + " into a " + IPv6Message.class.getName());
    }

    default boolean isIPv6() {
        return false;
    }
}
