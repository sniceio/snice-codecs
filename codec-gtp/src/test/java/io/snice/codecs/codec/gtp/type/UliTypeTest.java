package io.snice.codecs.codec.gtp.type;

import io.snice.buffer.Buffers;
import io.snice.codecs.codec.MccMnc;
import io.snice.codecs.codec.gtp.GtpTestBase;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.TypeLengthInstanceValue;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UliTypeTest extends GtpTestBase {

    @Test
    public void testUliParse() throws Exception {
        // remember, the "uli.raw" is the entire TLIV but we just want the value since
        // we are only testing the parsing of the actual UliType
        final var buffer = TypeLengthInstanceValue.frame(loadRaw("uli.raw")).getValue().getBuffer();
        assertThat(buffer.capacity(), is(13));

        final var uli = UliType.parse(buffer);

        final var tai = uli.getTai().get();
        final var tac = Buffers.wrap((byte) 0x91, (byte) 0x11);
        assertTai(tai, "310", "410", tac);

        final var ecgi = uli.getEcgi().get();
        assertEcgi(ecgi, "310", "410", Buffers.wrap((byte) 0x06, (byte) 0x88, (byte) 0x3E, (byte) 0x12));
    }

    @Test
    public void testUliCreate() throws Exception {

        // Only do TAI first...
        final var tac = Buffers.wrap((byte) 0x11, (byte) 0x22);
        final var tai = TaiField.of(MccMnc.of("123", "12"), tac);
        final var uli = UliType.create().withTai(tai).build();
        assertThat(uli.getBuffer().capacity(), is(1 + 5)); // 1 for header, 5 for TAI
        assertThat(uli.getEcgi(), is(Optional.empty()));

        final var parsed = UliType.parse(uli.getBuffer());
        assertTai(parsed.getTai().get(), "123", "12", tac);

        // Only do ECGI
        final var eci = Buffers.wrap((byte) 0x00, (byte) 0x11, (byte) 0xAA, (byte) 0xBB);
        final var ecgi = EcgiField.of(MccMnc.of("410", "310"), eci);
        final var uli2 = UliType.create().withEcgi(ecgi).build();
        assertThat(uli2.getBuffer().capacity(), is(1 + 7)); // 1 for header, 7 for ECGI
        assertThat(uli2.getTai(), is(Optional.empty()));

        final var parsed2 = UliType.parse(uli2.getBuffer());
        assertEcgi(parsed2.getEcgi().get(), "410", "310", eci);

        // Now encode them both
        final var uli3 = UliType.create().withEcgi(ecgi).withTai(tai).build();
        assertThat(uli3.getBuffer().capacity(), is(1 + 5 + 7)); // 1 for header, 5 for TAI, 7 for ECGI
        final var parsed3 = UliType.parse(uli3.getBuffer());
        assertTai(parsed3.getTai().get(), "123", "12", tac);
        assertEcgi(parsed3.getEcgi().get(), "410", "310", eci);
    }

}