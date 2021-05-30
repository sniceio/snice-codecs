package io.snice.codecs.codec.gtp.gtpc.v2.type;

import io.snice.buffer.Buffer;
import org.junit.Test;

import static io.snice.codecs.codec.gtp.gtpc.v2.type.RatType.Type.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RatTypeTest {

    @Test
    public void testCreate() {
        ensureRatType((byte) 1, UTRAN);
        ensureRatType("1", UTRAN);

        ensureRatType((byte) 2, GERAN);
        ensureRatType("2", GERAN);

        ensureRatType((byte) 3, WLAN);
        ensureRatType("3", WLAN);

        ensureRatType((byte) 4, GAN);
        ensureRatType("4", GAN);

        ensureRatType((byte) 5, HSPA_EVOLUTION);
        ensureRatType("5", HSPA_EVOLUTION);

        ensureRatType((byte) 6, EUTRAN);
        ensureRatType("6", EUTRAN);

        ensureRatType((byte) 7, VIRTUAL);
        ensureRatType("7", VIRTUAL);

        ensureRatType((byte) 8, EUTRAN_NB_IOT);
        ensureRatType("8", EUTRAN_NB_IOT);
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