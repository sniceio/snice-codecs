package io.snice.codecs.codec.gtp.gtpc.v1;

import io.snice.buffer.Buffer;
import io.snice.codecs.codec.Imsi;
import io.snice.codecs.codec.MccMnc;
import io.snice.codecs.codec.gtp.GtpTestBase;
import io.snice.codecs.codec.gtp.gtpc.v1.ie.tlv.GsnAddress;
import io.snice.codecs.codec.gtp.gtpc.v1.ie.tlv.MmContext;
import io.snice.codecs.codec.gtp.gtpc.v1.ie.tv.Rai;
import io.snice.codecs.codec.gtp.gtpc.v1.ie.tv.Tlli;
import io.snice.codecs.codec.gtp.gtpc.v1.ie.tv.TunnelEndpointIdentifierControlPlane;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class Gtp1MessageTest extends GtpTestBase {

    @Override
    @Before
    public void setUp() {
    }

    @Test
    public void testGtpv1Request() throws Exception {
        final var req = loadGtpv1Message("sgsn_context_request01.raw");
        assertThat(req.getInfoElements().size(), is(4));

        final var rai = req.getInfoElement(Rai.TYPE_VALUE).get().toTypeValue();
        assertThat(rai.getRaw(), is(Buffer.of((byte) 0x03, (byte) 0x32, (byte) 0xf4, (byte) 0x30, (byte) 0x01, (byte) 0xff, (byte) 0x73)));

        // Currently, RAI is of raw type and as such, we have to parse the actual raw value.
        // Once we go over all IE:s this will change but even then, we should always be able to
        // extract out the raw value buffer so this should always work.
        final var mccMnc = MccMnc.parseAsMccMnc(rai.getValue().getBuffer());
        assertThat(mccMnc, is(MccMnc.of("234", "03")));

        final var tlli = req.getInfoElement(Tlli.TYPE_VALUE).get().toTypeValue();
        assertThat(tlli.getRaw(), is(Buffer.of((byte) 0x04, (byte) 0x81, (byte) 0x00, (byte) 0x64, (byte) 0xd2)));

        final var teidControlPlane = req.getInfoElement(TunnelEndpointIdentifierControlPlane.TYPE_VALUE).get().toTypeValue();
        assertThat(teidControlPlane.getRaw(), is(Buffer.of((byte) 0x11, (byte) 0x5d, (byte) 0xf5, (byte) 0xa0, (byte) 0x6a)));

        final var gsnAddress = req.getInfoElement(GsnAddress.TYPE_VALUE).get().toTypeLengthValue();
        assertThat(gsnAddress.getValue().getBuffer().toIPv4String(0), is("10.36.10.17"));
    }

}
