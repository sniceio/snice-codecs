package io.snice.codecs.codec;

import io.snice.buffer.Buffer;
import io.snice.codecs.codec.internet.ipv4.IPv4Message;
import io.snice.codecs.codec.transport.UdpMessage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TestBase {

    public static Buffer loadRaw(final String resource) throws Exception {
        final Path path = Paths.get(TestBase.class.getResource(resource).toURI());
        final byte[] content = Files.readAllBytes(path);
        return Buffer.of(content);
    }

    public static void ensureIPv4Message(final IPv4Message ipv4, final String destIp, final String srcIp, final int ttl) {
        assertThat(ipv4.isIPv4(), is(true));
        assertThat(ipv4.getProtocol(), is(Protocol.UDP)); // only do UDP for now
        assertThat(ipv4.getDestinationIpAsString(), is(destIp));
        assertThat(ipv4.getSourceIpAsString(), is(srcIp));
        assertThat(ipv4.getTTL(), is(ttl));
    }

    public static void ensureUdpMessage(final UdpMessage udp, final int destPort, final int srcPort, final Buffer payload) {
        assertThat(udp.getSourcePort(), is(srcPort));
        assertThat(udp.getDestinationPort(), is(destPort));
        assertThat(udp.getPayload(), is(payload));
    }
}

