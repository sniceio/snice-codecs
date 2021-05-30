package io.snice.codecs.codec.gtp.gtpc;

import io.snice.codecs.codec.gtp.GtpRawData;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.Imsi;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.impl.RawTypeLengthInstanceValue;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ImsiTest {

    @Test
    public void testFrameIMSI() {
        // the full TLIV version of the IMSI
        final var imsi = Imsi.frame(GtpRawData.imsiTLIV).ensure().toImsi();
        assertThat(imsi.toString(), is("99999123456789"));
    }

    @Test
    public void testCreateImsi() {
        final var imsi = Imsi.ofValue("1234");
        assertThat(imsi.toString(), is("1234"));
        assertThat(imsi.getRaw().capacity(), is(4 + 2)); // 4 for TLIV header and 2 for the actual value

        final var raw = RawTypeLengthInstanceValue.frame(imsi.getRaw());
        final var imsi2 = raw.ensure().toImsi();
        assertThat(imsi2.toString(), is("1234"));
        assertThat(imsi2.getRaw().capacity(), is(4 + 2));
    }

    @Test
    public void testCreateImsiOddDigits() {
        final var imsi = Imsi.ofValue("12345");
        assertThat(imsi.toString(), is("12345"));
        assertThat(imsi.getRaw().capacity(), is(4 + 3)); // 4 for header and 3 for the values

        final var raw = RawTypeLengthInstanceValue.frame(imsi.getRaw());
        final var imsi2 = raw.ensure().toImsi();
        assertThat(imsi2.toString(), is("12345"));
        assertThat(imsi2.getRaw().capacity(), is(4 + 3));
    }
}
