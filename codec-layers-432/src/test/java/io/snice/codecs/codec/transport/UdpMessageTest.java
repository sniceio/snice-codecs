package io.snice.codecs.codec.transport;

import io.snice.buffer.Buffers;
import io.snice.codecs.codec.RawData;
import io.snice.codecs.codec.TestBase;
import io.snice.codecs.codec.internet.ipv4.IPv4Message;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UdpMessageTest extends TestBase {

    @Test
    public void testUdpHelloWorld() {
        final var udp = UdpMessage.frame(RawData.rawIPv4UdpPayloadOnly);
        assertThat(udp.getDestinationPort(), is(1234));
        assertThat(udp.getSourcePort(), is(38896));
        assertThat(udp.getChecksum(), is(Integer.parseInt("fe27", 16)));
        assertThat(udp.getPayload().toString(), is("hello world\n"));
    }

    @Test
    public void testCreateUdpIPv4Message() {
        final var ipv4 = UdpMessage.createUdpIPv4("one two three")
                .withDestinationPort(1234)
                .withSourcePort(1111)
                .withTTL(34)
                .withDestinationIp("10.11.12.13")
                .withSourceIp("22.23.24.25")
                .build();
        final var udp = ipv4.getPayload();

        ensureIPv4Message(ipv4, "10.11.12.13", "22.23.24.25", 34);
        ensureUdpMessage(udp, 1234, 1111, Buffers.wrap("one two three"));

        // re-frame it and ensure it is correct.
        final var ipv4Reframed = IPv4Message.frame(ipv4.getBuffer());
        ensureIPv4Message(ipv4Reframed, "10.11.12.13", "22.23.24.25", 34);

        final var udpReframed = UdpMessage.frame(ipv4Reframed.getPayload());
        ensureUdpMessage(udpReframed, 1234, 1111, Buffers.wrap("one two three"));
    }

}