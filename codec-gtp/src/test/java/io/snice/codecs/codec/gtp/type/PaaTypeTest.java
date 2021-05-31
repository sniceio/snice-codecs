package io.snice.codecs.codec.gtp.type;

import io.snice.codecs.codec.gtp.GtpTestBase;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.TypeLengthInstanceValue;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PaaTypeTest extends GtpTestBase {

    @Test
    public void testParse() throws Exception {
        final var buffer = TypeLengthInstanceValue.frame(loadRaw("paa.raw")).getValue().getBuffer();
        final var paa = PaaType.parse(buffer);
        ensurePaa(paa, PdnType.Type.IPv4, "0.0.0.0");

        final var paa2 = PaaType.fromIPv4("10.11.12.13");
        ensurePaa(paa2, PdnType.Type.IPv4, "10.11.12.13");

        final var paa3 = PaaType.parse(paa2.getBuffer());
        ensurePaa(paa3, PdnType.Type.IPv4, "10.11.12.13");
    }

    public static void ensurePaa(final PaaType paa, final PdnType.Type type, final String ipv4) {
        assertThat(paa.getPdnType().getType(), is(type));
        assertThat(paa.getIPv4Address().get().toIPv4String(0), is(ipv4));

    }

}