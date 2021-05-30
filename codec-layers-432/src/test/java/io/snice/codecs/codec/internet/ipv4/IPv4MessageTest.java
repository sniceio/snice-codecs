package io.snice.codecs.codec.internet.ipv4;

import io.snice.buffer.Buffers;
import io.snice.codecs.codec.Protocol;
import io.snice.codecs.codec.RawData;
import io.snice.codecs.codec.TestBase;
import io.snice.codecs.codec.internet.IpMessage;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class IPv4MessageTest extends TestBase {

    @Test
    public void frameIpv4Message() throws Exception {
        final var ipv4 = IpMessage.frame(RawData.rawIPv4Udp).toIPv4();
        assertThat(ipv4.isIPv4(), is(true));
        assertThat(ipv4.getProtocol(), is(Protocol.UDP));
        assertThat(ipv4.getIpChecksum(), is(56310)); // hex: dbf6
        assertThat(ipv4.isFragmented(), is(false));
        assertThat(ipv4.getFragmentOffset(), is((short) 0));
        assertThat(ipv4.getTotalLength(), is(40));
        assertThat(ipv4.getPayloadLength(), is(20));
        assertThat(ipv4.getTTL(), is(64));
        assertThat(ipv4.getPayload(), is(RawData.rawIPv4UdpPayloadOnly));
    }

    @Test
    public void frameIpv4Message2() throws Exception {
        final var ipv4 = IpMessage.frame(RawData.ipv4Buffer).toIPv4();
        assertThat(ipv4.isIPv4(), is(true));
        assertThat(ipv4.getProtocol(), is(Protocol.UDP));
        assertThat(ipv4.getSourceIpAsString(), is("127.0.0.1"));
        assertThat(ipv4.getSourceIp(), is(Buffers.wrapAsIPv4("127.0.0.1")));

        assertThat(ipv4.getDestinationIpAsString(), is("127.0.0.1"));
        assertThat(ipv4.getDestinationIp(), is(Buffers.wrapAsIPv4("127.0.0.1")));
    }

}