package io.snice.codecs.codec.gtp.gtpc.v1;

import io.snice.buffer.Buffer;
import io.snice.buffer.Buffers;
import io.snice.codecs.codec.Protocol;
import io.snice.codecs.codec.gtp.GtpMessage;
import io.snice.codecs.codec.gtp.GtpRawData;
import io.snice.codecs.codec.gtp.GtpTestBase;
import io.snice.codecs.codec.gtp.Teid;
import io.snice.codecs.codec.gtp.gtpc.v1.impl.ImmutableGtp1Message;
import io.snice.codecs.codec.internet.IpMessage;
import io.snice.codecs.codec.transport.UdpMessage;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class GtpuMessageTest extends GtpTestBase {

    @Test
    public void testCreateGtpU() throws Exception {
        final var seqNo = Buffers.random(2);
        final var teid = Teid.random();
        final var payload = Buffers.wrap("hello world");
        final var gtpU = ImmutableGtp1Message.create(Gtp1MessageType.G_PDU)
                .withTeid(teid)
                .withPayload(payload)
                .withSeqNo(seqNo)
                .build();

        final var header = gtpU.getHeader().toGtp1Header();
        assertGtpGPduHeader(header, 4 + payload.capacity(), payload.capacity(), teid.getBuffer(), seqNo);

        final var gtp = GtpMessage.frame(gtpU.getBuffer());
        assertGtpGPduHeader(gtp.getHeader().toGtp1Header(), 4 + payload.capacity(), payload.capacity(), teid.getBuffer(), seqNo);
    }

    @Test
    public void testParseGtpPdu() {
        final Gtp1Message pdu = GtpMessage.frame(GtpRawData.pduReqDnsGoogleDotCom).toGtp1Message();
        assertThat(pdu.getType(), is(Gtp1MessageType.G_PDU));
        final var payload = pdu.getPayload().get();
        final var ipv4 = IpMessage.frame(payload).toIPv4();
        assertThat(ipv4.getProtocol(), is(Protocol.UDP));
        assertThat(ipv4.isUDP(), is(true));

        assertThat(ipv4.getDestinationIpAsString(), is("8.8.8.8"));

        final var udp = UdpMessage.frame((Buffer) ipv4.getPayload());
        assertThat(udp.getDestinationPort(), is(53));
        final var udpPayload = udp.getPayload();

        // extracted from wireshark...
        final var expectedPayload = Buffer.of(
                (byte) 0x5c, (byte) 0x79, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x77, (byte) 0x77, (byte) 0x77,
                (byte) 0x06, (byte) 0x67, (byte) 0x6f, (byte) 0x6f, (byte) 0x67, (byte) 0x6c, (byte) 0x65, (byte) 0x03,
                (byte) 0x63, (byte) 0x6f, (byte) 0x6d, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01);

        assertThat(udpPayload, is(expectedPayload));

    }
}
