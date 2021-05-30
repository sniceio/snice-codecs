package io.snice.codecs.codec;

import io.snice.buffer.Buffer;
import io.snice.buffer.Buffers;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ImsiTest {

    // Jersey Telecom - 234/50 and just a bunch of 1s as the MSIN
    private static final Buffer jtImsi01 = Buffers.wrap((byte) 0x32, (byte) 0x54, (byte) 0x10, (byte) 0x11, (byte) 0x11, (byte) 0x11, (byte) 0x11, (byte) 0x11);

    @Test(expected = IllegalArgumentException.class)
    public void testBadImsiNegative() {
        Imsi.of(MccMnc.of("001", "001"), -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadImsiTooLarge() {
        Imsi.of(MccMnc.of("001", "001"), 1000000000);
    }

    @Test
    public void testParseImsiFromBuffer() {
        final var imsi = Imsi.of(jtImsi01);
        assertThat(imsi.getMccMnc(), is(MccMnc.of("234", "50")));
        final var imsiExpected = Imsi.of("234/50", "11111111111");
        assertThat(imsi, is(imsiExpected));
        assertThat(imsi.getMsin(), is("11111111111"));
        assertThat(imsi.toString(), is("2345011111111111"));
    }

    @Test
    public void testCreateFromStrings() {
        ensureCreateImsi("310/410", "1234567890");
        ensureCreateImsi("310410", "1234567890");
    }

    private void ensureCreateImsi(final String mccMnc, final String msin) {

        // strip 4th position if it is not a digit
        final var expectedMccMnc = Character.isDigit(mccMnc.charAt(3)) ? mccMnc : mccMnc.substring(0, 3) + mccMnc.substring(4);

        final var imsi = Imsi.of(mccMnc, msin);
        assertThat(imsi.getMccMnc().getAsString(), is(expectedMccMnc));
        assertThat(imsi.getMsin(), is(msin));

        assertThat(imsi.toString(), is(expectedMccMnc + msin));

    }

    @Test
    public void testParseImsi() {
        final var imsi = Imsi.of(Buffers.wrapAsTbcd("0010011234567890"));
        assertThat(imsi.toString(), is("0010011234567890"));
        assertThat(imsi.getMccMnc().getMcc(), is("001"));
        assertThat(imsi.getMccMnc().getMnc(), is("001"));
        assertThat(imsi.getMsin(), is("1234567890"));
    }

    @Test
    public void testCreateImsi() {
        final var mccMnc = MccMnc.of("001", "001");

        ensureImsi(Imsi.of(mccMnc, 0), "001001000000000", "000000000");
        ensureImsi(Imsi.of(mccMnc, 1), "001001000000001", "000000001");
        ensureImsi(Imsi.of(mccMnc, 9), "001001000000009", "000000009");

        ensureImsi(Imsi.of(mccMnc, 10), "001001000000010", "000000010");
        ensureImsi(Imsi.of(mccMnc, 11), "001001000000011", "000000011");
        ensureImsi(Imsi.of(mccMnc, 19), "001001000000019", "000000019");

        ensureImsi(Imsi.of(mccMnc, 20), "001001000000020", "000000020");
        ensureImsi(Imsi.of(mccMnc, 21), "001001000000021", "000000021");
        ensureImsi(Imsi.of(mccMnc, 29), "001001000000029", "000000029");

        ensureImsi(Imsi.of(mccMnc, 120), "001001000000120", "000000120");
        ensureImsi(Imsi.of(mccMnc, 121), "001001000000121", "000000121");
        ensureImsi(Imsi.of(mccMnc, 129), "001001000000129", "000000129");

        ensureImsi(Imsi.of(mccMnc, 920), "001001000000920", "000000920");
        ensureImsi(Imsi.of(mccMnc, 921), "001001000000921", "000000921");
        ensureImsi(Imsi.of(mccMnc, 929), "001001000000929", "000000929");

        ensureImsi(Imsi.of(mccMnc, 100000920), "001001100000920", "100000920");
        ensureImsi(Imsi.of(mccMnc, 100000921), "001001100000921", "100000921");
        ensureImsi(Imsi.of(mccMnc, 100000929), "001001100000929", "100000929");

        ensureImsi(Imsi.of(mccMnc, 900000920), "001001900000920", "900000920");
        ensureImsi(Imsi.of(mccMnc, 900000921), "001001900000921", "900000921");
        ensureImsi(Imsi.of(mccMnc, 900000929), "001001900000929", "900000929");

        ensureImsi(Imsi.of(mccMnc, 900100920), "001001900100920", "900100920");
        ensureImsi(Imsi.of(mccMnc, 900100921), "001001900100921", "900100921");
        ensureImsi(Imsi.of(mccMnc, 900100929), "001001900100929", "900100929");

        // the "largest" IMSI we can create
        ensureImsi(Imsi.of(mccMnc, 999999999), "001001999999999", "999999999");
    }


    private void ensureImsi(final Imsi imsi, final String expected, final String expectedMsin) {
        assertThat(imsi.toString(), is(expected));
        assertThat(imsi.getMsin(), is(expectedMsin));

    }
}