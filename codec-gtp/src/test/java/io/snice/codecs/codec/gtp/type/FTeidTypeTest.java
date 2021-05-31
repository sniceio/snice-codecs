package io.snice.codecs.codec.gtp.type;

import io.snice.buffer.Buffers;
import io.snice.codecs.codec.gtp.GtpTestBase;
import io.snice.codecs.codec.gtp.Teid;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.FTeid;
import io.snice.codecs.codec.tgpp.ReferencePoint;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FTeidTypeTest extends GtpTestBase {

    @Test
    public void createFTeid() {
        final var tied = FTeidType.create()
                .withIPv4Address("192.168.0.100")
                .withReferencePoint(ReferencePoint.S5, true)
                .withTeid(Teid.ZEROS)
                .build();

        assertThat(tied.getIpv4AddressAsString().get(), is("192.168.0.100"));
        assertThat(tied.getIpv4Address().get(), is(Buffers.wrapAsIPv4("192.168.0.100")));
        assertThat(tied.getTeid(), is(Teid.ZEROS));

        final var fteid2 = FTeidType.parse(tied.getBuffer());
        assertThat(fteid2.getIpv4AddressAsString().get(), is("192.168.0.100"));
        assertThat(fteid2.getIpv4Address().get(), is(Buffers.wrapAsIPv4("192.168.0.100")));
        assertThat(fteid2.getTeid(), is(Teid.ZEROS));
    }

    @Test
    public void checkFTeid() throws Exception {
        // all of the values have been verified using wireshark
        final var fteid = FTeid.frame(loadRaw("fteid.raw"));
        final var type = fteid.getValue();
        assertThat(type.getTeid(), is(Teid.of(Buffers.wrap((byte) 0x00, (byte) 0x00, (byte) 0xdc, (byte) 0x0e))));
        assertThat(type.getIpv4AddressAsString().get(), is("13.52.50.32"));
        assertThat(type.getIpv6Address(), is(Optional.empty()));
    }

    @Test
    public void checkFTeid2() throws Exception {
        // all of the values have been verified using wireshark
        // and the following byte-array has been grabbed from wireshark using fake traffic
        // that wireshark seems to agree is ok and true
        final var buffer = Buffers.wrap((byte) 0x57, (byte) 0x00, (byte) 0x09, (byte) 0x01, (byte) 0x87,
                (byte) 0x00, (byte) 0x91, (byte) 0x36, (byte) 0xfd, (byte) 0x0A, (byte) 0x24, (byte) 0x0A, (byte) 0x7b);

        final var fteid = FTeid.frame(buffer);
        final var type = fteid.getValue();
        assertThat(type.getTeid(), is(Teid.of(Buffers.wrap((byte) 0x00, (byte) 0x91, (byte) 0x36, (byte) 0xfd))));
        final var ipv4Address = Buffers.wrap((byte) 0x0A, (byte) 0x24, (byte) 0x0A, (byte) 0x7b);
        assertThat(type.getIpv4Address().get(), is(ipv4Address));
        assertThat(type.getIpv4AddressAsString().get(), is("10.36.10.123"));
        assertThat(type.getIpv6Address(), is(Optional.empty()));
    }

}