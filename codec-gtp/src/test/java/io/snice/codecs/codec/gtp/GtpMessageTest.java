package io.snice.codecs.codec.gtp;

import io.snice.buffer.Buffer;
import io.snice.buffer.Buffers;
import io.snice.buffer.impl.EmptyBuffer;
import io.snice.codecs.codec.gtp.gtpc.InfoElement;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2InfoElement;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Message;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2MessageType;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Response;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.FTeid;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.Imsi;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.Msisdn;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.TypeLengthInstanceValue;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

public class GtpMessageTest extends GtpTestBase {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }


    @Test
    public void testBadInput() {
        ensureBlowsUp(null);
        ensureBlowsUp(EmptyBuffer.EMPTY);

        // cut down the buffer. We will be able to parse the header
        // but not frame the rest.
        ensureBlowsUp(GtpRawData.createSessionRequest.slice(50));
    }

    private void ensureBlowsUp(final Buffer buffer) {
        try {
            Gtp2Message.frame(buffer);
            fail("Expected the framing to blow up");
        } catch (final IllegalArgumentException | GtpParseException e) {
            // expected
        }
    }

    @Test
    public void testBuildGtpv2Message() {
        final var teidBuffer = Buffers.wrap((byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44);
        final var csr = Gtp2Message.create(Gtp2MessageType.CREATE_SESSION_REQUEST)
                .withTeid(teidBuffer)
                .withTliv(Imsi.ofValue("001001123456"))
                .withTliv(Msisdn.ofValue("41555512345"))
                .build();

        assertThat(csr.isRequest(), is(true));

        final var reParsed = GtpMessage.frame(csr.getBuffer()).toGtp2Message();
        final var header = reParsed.getHeader();
        assertThat(header.getVersion(), is(2));
        assertThat(header.getMessageTypeDecimal(), is(Gtp2MessageType.CREATE_SESSION_REQUEST.getType()));
        assertThat(reParsed.getImsi().get().toString(), is("001001123456"));
        assertThat(reParsed.getInfoElement(Msisdn.TYPE).get().ensure().toString(), is("41555512345"));
        assertThat(reParsed.isRequest(), is(true));
    }

    @Test
    public void testParseCreateSessionResponse() throws Exception {
        final Gtp2Response response = GtpMessage.frame(loadRaw("create_session_response.raw")).toGtp2Response();
        assertThat(response.getCause().get().getValue().getCauseValue(), is(16));

        final var maybeFTeid = response.getInfoElement(FTeid.TYPE, 1).map(TypeLengthInstanceValue::toFTeid);
        assertThat("Unable to find the FTEID for GTP-C", maybeFTeid, not(Optional.empty()));
        final var fteid = maybeFTeid.map(FTeid::getValue).get();
        assertThat(fteid.getIpv4AddressAsString().get(), is("172.22.189.47"));
        assertThat(fteid.getTeid(), is(Teid.of((byte) 0x00, (byte) 0x1f, (byte) 0x0b, (byte) 0x38)));
    }

    @Test
    public void testParseGtpv2Message() {
        final Gtp2Message msg = GtpMessage.frame(GtpRawData.createSessionRequest).toGtp2Message();
        assertThat(msg.getHeader().getLength(), is(251));

        final List<? extends InfoElement> ie = msg.getInfoElements();
        assertThat(ie.size(), is(16));
        assertInfoElement(ie.get(0), 1, 8);
        assertInfoElement(ie.get(1), 75, 8);
        assertInfoElement(ie.get(2), 86, 13);
        assertInfoElement(ie.get(3), 83, 3);
        assertInfoElement(ie.get(4), 82, 1);
        assertInfoElement(ie.get(5), 87, 9);
        assertInfoElement(ie.get(6), 71, 39);
        assertInfoElement(ie.get(7), 128, 1);
        assertInfoElement(ie.get(8), 99, 1);
        assertInfoElement(ie.get(9), 79, 5);
        assertInfoElement(ie.get(10), 127, 1);
        assertInfoElement(ie.get(11), 72, 8);
        assertInfoElement(ie.get(12), 78, 35);
        assertInfoElement(ie.get(13), 93, 44);
        assertInfoElement(ie.get(14), 3, 1);
        assertInfoElement(ie.get(15), 114, 2);

        assertThat(msg.isCreateSessionRequest(), is(true));
        assertIMSI(msg.getInfoElement(Gtp2InfoElement.IMSI).get(), "234500011999000");
        assertIMSI(msg.getInfoElement(Gtp2InfoElement.IMSI).get().ensure().toTliv(), "234500011999000");
    }

    @Test
    public void testGTPc2() {
        final var msg = GtpMessage.frame(GtpRawData.gtpc2).toGtp2Message();
        assertThat(msg.getHeader().getLength(), is(240));
        assertThat(msg.isGtpVersion2(), is(true));
        assertThat(msg.toGtp2Message().isCreateSessionRequest(), is(true));

        assertIMSI(msg.getImsi().get(), "99999123456789");
    }

    private static void assertIMSI(final TypeLengthInstanceValue ie, final String expected) {
        assertThat(ie.isImsi(), is(true));
        assertThat(ie.ensure().toTliv().toImsi().toString(), is(expected));
    }

    private static void assertInfoElement(final InfoElement ie, final int expectedType, final int expectedLength) {
        assertThat(ie.getTypeAsDecimal(), is(expectedType));
        assertThat(ie.getLength(), is(expectedLength));
    }


}