package io.snice.codecs.codec.gtp.type;

import io.snice.buffer.Buffer;
import io.snice.codecs.codec.gtp.GtpTestBase;
import org.junit.Test;

public class ArpTypeTest extends GtpTestBase {

    @Test
    public void testArp() {
        final var arp = ArpType.parse(Buffer.of((byte) 0x48));
        ensureArp(arp, 2, false, true);

        final var arp2 = ArpType.ofValue(7, true, true);
        ensureArp(arp2, 7, true, true);

        final var arp3 = ArpType.parse(arp2.getBuffer());
        ensureArp(arp3, 7, true, true);
    }

}