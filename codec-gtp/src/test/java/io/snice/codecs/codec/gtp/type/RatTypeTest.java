package io.snice.codecs.codec.gtp.type;

import io.snice.buffer.Buffer;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RatTypeTest {

    @Test
    public void testCreate() {
        ensureRatType((byte) 1, RatType.Type.UTRAN);
        ensureRatType("1", RatType.Type.UTRAN);

        ensureRatType((byte) 2, RatType.Type.GERAN);
        ensureRatType("2", RatType.Type.GERAN);

        ensureRatType((byte) 3, RatType.Type.WLAN);
        ensureRatType("3", RatType.Type.WLAN);

        ensureRatType((byte) 4, RatType.Type.GAN);
        ensureRatType("4", RatType.Type.GAN);

        ensureRatType((byte) 5, RatType.Type.HSPA_EVOLUTION);
        ensureRatType("5", RatType.Type.HSPA_EVOLUTION);

        ensureRatType((byte) 6, RatType.Type.EUTRAN);
        ensureRatType("6", RatType.Type.EUTRAN);

        ensureRatType((byte) 7, RatType.Type.VIRTUAL);
        ensureRatType("7", RatType.Type.VIRTUAL);

        ensureRatType((byte) 8, RatType.Type.EUTRAN_NB_IOT);
        ensureRatType("8", RatType.Type.EUTRAN_NB_IOT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadValue0Str() {
        RatType.parse("0");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadValue0Byte() {
        RatType.parse(Buffer.of((byte) 0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadValue9Str() {
        RatType.parse("9");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadValue9Byte() {
        RatType.parse(Buffer.of((byte) 9));
    }

    private static void ensureRatType(final byte b, final RatType.Type expected) {
        assertThat(RatType.parse(Buffer.of(b)).getType(), is(expected));
    }

    private static void ensureRatType(final String s, final RatType.Type expected) {
        assertThat(RatType.parse(s).getType(), is(expected));
    }

}