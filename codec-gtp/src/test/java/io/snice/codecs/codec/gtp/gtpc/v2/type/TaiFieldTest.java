package io.snice.codecs.codec.gtp.gtpc.v2.type;

import io.snice.buffer.Buffer;
import io.snice.buffer.Buffers;
import io.snice.codecs.codec.MccMnc;
import io.snice.codecs.codec.gtp.GtpTestBase;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TaiFieldTest extends GtpTestBase  {

    @Test
    public void testTai() {
        // taken from wireshark
        var tai = TaiField.parse(Buffers.wrap((byte) 0x13, (byte) 0x00, (byte) 0x14, (byte) 0x91, (byte) 0x11));

        var tac = Buffers.wrap((byte) 0x91, (byte) 0x11);
        assertTai(tai, "310", "410", tac);

        tac = Buffers.wrap((byte) 0x11, (byte) 0x22);
        tai = TaiField.of(MccMnc.of("123", "12"), tac);
        assertTai(tai, "123", "12", tac);

        tai = TaiField.parse(tai.getBuffer());
        assertTai(tai, "123", "12", tac);
    }
}