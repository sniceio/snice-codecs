package io.snice.codecs.codec.gtp.gtpc;

import io.snice.buffer.Buffer;
import io.snice.codecs.codec.gtp.GtpRawData;
import io.snice.codecs.codec.gtp.GtpTestBase;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.TypeLengthInstanceValue;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TypeLengthInstanceValueTest extends GtpTestBase {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testParseImsiTLIV() {
        ensureBasicTlivProperties(GtpRawData.imsiTLIV, 1, 7);
        ensureBasicTlivProperties(GtpRawData.userLocationInfo, 86, 13);
        ensureBasicTlivProperties(GtpRawData.servingNetwork, 83, 3);
        ensureBasicTlivProperties(GtpRawData.fteid, 87, 9);
    }

    @Test
    public void testParseIMSI() {
        final TypeLengthInstanceValue tliv = TypeLengthInstanceValue.frame(GtpRawData.imsiTLIV);
        assertThat(tliv.isImsi(), is(true));
        final var imsi = tliv.ensure().toTliv().toImsi();

    }

    private static void ensureBasicTlivProperties(final Buffer buffer, final int expectedType, final int expectedLength) {
        final TypeLengthInstanceValue tliv = TypeLengthInstanceValue.frame(buffer);
        assertThat(tliv.getTypeAsDecimal(), is(expectedType));
        assertThat(tliv.getLength(), is(expectedLength));
    }
}