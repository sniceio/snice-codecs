package io.snice.codecs.codec;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class IccidTest {

    @Test
    public void testCreateIccid() {
        ensureIccid(Iccid.of(0), "00000000000000000000");
        ensureIccid(Iccid.of(1), "00000000000000000001");
        ensureIccid(Iccid.of(9), "00000000000000000009");

        ensureIccid(Iccid.of(10), "00000000000000000010");
        ensureIccid(Iccid.of(11), "00000000000000000011");
        ensureIccid(Iccid.of(19), "00000000000000000019");

        ensureIccid(Iccid.of(90), "00000000000000000090");
        ensureIccid(Iccid.of(91), "00000000000000000091");
        ensureIccid(Iccid.of(99), "00000000000000000099");

        ensureIccid(Iccid.of(190), "00000000000000000190");
        ensureIccid(Iccid.of(191), "00000000000000000191");
        ensureIccid(Iccid.of(199), "00000000000000000199");

        ensureIccid(Iccid.of(1000190), "00000000000001000190");
        ensureIccid(Iccid.of(1000191), "00000000000001000191");
        ensureIccid(Iccid.of(1000199), "00000000000001000199");

        // largest one you can create using a long
        ensureIccid(Iccid.of(999999999999999999L), "00999999999999999999");
    }

    private static void ensureIccid(final Iccid iccid, final String expected) {
        assertThat(iccid.toString(), is(expected));
    }

}