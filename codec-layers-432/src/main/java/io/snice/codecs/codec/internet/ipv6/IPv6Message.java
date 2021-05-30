package io.snice.codecs.codec.internet.ipv6;

import io.snice.codecs.codec.internet.IpMessage;

public interface IPv6Message extends IpMessage {

    @Override
    default IPv6Message toIPv6() {
        return this;
    }

    @Override
    default boolean isIPv6() {
        return true;
    }
}
