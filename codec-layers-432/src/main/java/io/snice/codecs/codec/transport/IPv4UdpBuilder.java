package io.snice.codecs.codec.transport;

import io.snice.codecs.codec.internet.ipv4.IPv4Builder;

public interface IPv4UdpBuilder extends IPv4Builder<UdpMessage> {

    IPv4UdpBuilder withSourcePort(int port);

    IPv4UdpBuilder withDestinationPort(int port);

}
