package io.snice.codecs.codec.gtp.gtpc.v2.type;

import io.snice.buffer.Buffers;
import io.snice.codecs.codec.gtp.GtpTestBase;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.Ebi;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EbiTypeTest extends GtpTestBase {

    @Test
    public void testEbi() {
        // raw grabbed from wireshark
        final var raw = Buffers.wrap((byte) 0x49, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x05);
        final var ebi = Ebi.frame(raw);
        assertThat(ebi.getValue().getId(), is(5));

        final var ebiType = EbiType.ofValue(3);
        assertThat(ebiType.getId(), is(3));
        assertThat(ebiType.getBuffer(), is(Buffers.wrap((byte) 0x03)));

        for (int i = 0; i < 16; ++i) {
            assertThat(EbiType.ofValue(i).getId(), is(i));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalLessThanZero() {
        EbiType.ofValue(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalGreaterThan15() {
        EbiType.ofValue(16);
    }

}
