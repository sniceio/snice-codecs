package io.snice.codecs.codec.gtp.gtpc.v1;

import io.snice.buffer.Buffer;
import io.snice.buffer.Buffers;
import io.snice.codecs.codec.gtp.GtpHeader;
import io.snice.codecs.codec.gtp.GtpRawData;
import io.snice.codecs.codec.gtp.GtpTestBase;
import io.snice.codecs.codec.gtp.Teid;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author jonas@jonasborjesson.com
 */
public class Gtp1HeaderTest extends GtpTestBase {


    @Test
    public void testFrameGtpV1Header() {
        final Gtp1Header header = GtpHeader.frame(GtpRawData.createPdpContextRequest).toGtp1Header();
        assertThat(header.getVersion(), is(1));
        assertThat(header.getLength(), is(180));
        assertThat(header.getTotalLength(), is(180 + 8));
        assertThat(header.getBodyLength(), is(180 - 4));
        assertThat(header.getMessageTypeDecimal(), is(16));
        assertThat(header.getSequenceNo().get(), is(Buffer.of((byte) 0x6a, (byte) 0xf3)));
        assertThat(header.getSequenceNoAsDecimal().get(), is(27379));

        assertThat(header.toGtp1Header().getTeid(), is(Teid.ZEROS));
    }

    @Test
    public void testFrameGtpuHeader() throws Exception {
        assertGtpGPduHeader("gtp_u_dns_req_google_dot_com.raw", 60, 0x64, 0xEE, 0x05, 0xA4);
        assertGtpGPduHeader("gtp_u_dns_resp_google_dot_com.raw", 76, 0xF4, 0xE4, 0x77, 0x22);
    }

    @Test
    public void testCreateGtpuHeader() {
        final var buffer = Buffers.random(4);
        final var header = Gtp1Header.of(Gtp1MessageType.G_PDU)
                .withTeid(Teid.of(buffer))
                .withBodySize(123)
                .build();
        assertGtpGPduHeader(header, 123, buffer);
    }

    @Test
    public void testCreateGtpuHeaderWithSeqNo() {
        final var teid = Buffers.random(4);
        final var seqNo = Buffers.random(2);
        final var header = Gtp1Header.of(Gtp1MessageType.G_PDU)
                .withTeid(teid)
                .withSequenceNumber(seqNo)
                .withBodySize(789)
                .build();
        // now + 4 because header grew due to seqNo being present
        // note, that expected length is what is being encoded into the header.
        // The length of the body is still 789. See comment in javadoc...
        // also note that the assert will add 8 for the default/minimum length of
        // the header.
        assertGtpGPduHeader(header, 789 + 4, 789, teid, seqNo);
    }

}
