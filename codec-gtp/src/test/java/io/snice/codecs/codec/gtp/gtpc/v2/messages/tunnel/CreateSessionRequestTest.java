package io.snice.codecs.codec.gtp.gtpc.v2.messages.tunnel;

import io.snice.buffer.Buffers;
import io.snice.codecs.codec.gtp.GtpMessage;
import io.snice.codecs.codec.gtp.GtpTestBase;
import io.snice.codecs.codec.gtp.Teid;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Header;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Message;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2MessageType;
import io.snice.codecs.codec.gtp.type.RatType;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CreateSessionRequestTest extends GtpTestBase {

    @Test
    public void testCreateCSR() {

        final var csr = someCSR();

        assertThat(csr.isCreateSessionRequest(), is(true));

        final var reParsed = GtpMessage.frame(csr.getBuffer()).toCreateSessionRequest();
        assertThat(reParsed.isRequest(), is(true));
        ensureMsisdn(reParsed.getMsisdn(), "4155551234");
        ensureImsi(reParsed.getImsi(), "001001123456789");

        assertThat(reParsed.isCreateSessionRequest(), is(true));

        final var bearer = csr.getBearerContext().get();
        final var bearerQos = bearer.getValue().getBearerQos().get();
        final var bearerFTeid = bearer.getValue().getBearerFTeid().get();
        final var qos = bearerQos.getValue().getQos();
        final var arp = bearerQos.getValue().getArp();
        ensureQos(qos, 9, 0, 0, 0, 0);
        ensureArp(arp, 10, true, false);
        ensureFTeid(bearerFTeid, "20.30.40.50", gtpuTeid);

        ensureFTeid(reParsed.getSenderFTeid(), "10.20.30.40", teid);
    }

    /**
     * Ensure that if we try and create the CSR via the more generic
     * "kick-off" message {@link Gtp2Message#create(Gtp2MessageType)} that we
     * still create an actual {@link CreateSessionRequest}
     */
    @Test
    public void testCreateCsr2() {
        final var csr = Gtp2Message.create(Gtp2MessageType.CREATE_SESSION_REQUEST)
                .withTeid(Teid.ZEROS)
                .withRat(RatType.EUTRAN)
                .build()
                .toCreateSessionRequest();

        assertThat(csr.isCreateSessionRequest(), is(true));
    }

    @Test
    public void testCreateCsr3() {
        final var seqNo = Buffers.random(3);
        final var teid = Teid.random();
        final var header = Gtp2Header.of(Gtp2MessageType.CREATE_SESSION_REQUEST)
                .withSequenceNumber(seqNo)
                .withTeid(teid)
                .build();

        final var csr = Gtp2Message.create(header)
                .withRat(RatType.EUTRAN)
                .build()
                .toCreateSessionRequest();

        assertThat(csr.isCreateSessionRequest(), is(true));
        assertThat(csr.getHeader().getSequenceNo(), is(seqNo));
        assertThat(csr.getHeader().getTeid().get(), is(teid));
    }
}
