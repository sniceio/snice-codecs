package io.snice.codecs.codec.gtp.gtpc.v2.type;

import io.snice.buffer.Buffer;
import org.junit.Test;

import static io.snice.codecs.codec.gtp.gtpc.v2.type.PdnType.Type.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PdnTypeTest {


    @Test
    public void testCreate() {
        ensurePdnType((byte) 1, IPv4);
        ensurePdnType("1", IPv4);

        ensurePdnType((byte) 2, IPv6);
        ensurePdnType("2", IPv6);

        ensurePdnType((byte) 3, IPv4v6);
        ensurePdnType("3", IPv4v6);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadValue0Str() {
        PdnType.parse("0");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadValue0Byte() {
        PdnType.parse(Buffer.of((byte) 0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadValue4Str() {
        PdnType.parse("4");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadValue4Byte() {
        PdnType.parse(Buffer.of((byte) 4));
    }


    private static void ensurePdnType(final byte b, final PdnType.Type expected) {
        assertThat(PdnType.parse(Buffer.of(b)).getType(), is(expected));
    }

    private static void ensurePdnType(final String s, final PdnType.Type expected) {
        assertThat(PdnType.parse(s).getType(), is(expected));
    }
}
