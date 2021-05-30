package io.snice.codecs.codec.gtp.gtpc.v2;

import io.snice.buffer.Buffer;
import io.snice.buffer.Buffers;
import io.snice.codecs.codec.gtp.*;
import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1Header;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.Optional;
import java.util.function.Supplier;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * @author jonas@jonasborjesson.com
 */
public class Gtp2HeaderTest extends GtpTestBase {

    private final Teid emptyTeid = Teid.of(Buffer.of((byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00));

    @Test
    public void testCreateGtpv2Header() {
        final var builder = Gtp2Header.of(Gtp2MessageType.CREATE_SESSION_REQUEST);
        builder.withTlivSize(100);
        builder.withSequenceNumber(Buffers.wrap((byte) 0x11, (byte) 0x22, (byte) 0x33));
        final var header = builder.build();

        assertThat(header.getVersion(), is(2));
        assertThat(header.getLength(), is(100 + 3 + 1)); // no TEID. +3 is for seqNo and +1 for spare
        assertThat(header.getTotalLength(), is(100 + 3 + 1 + 4)); // no TEID and the 4 mandatory bytes for the header
        assertThat(header.getBuffer().capacity(), is(8)); // 8 because we have no TEID
        assertThat(header.getSequenceNo(), is(Buffers.wrap((byte) 0x11, (byte) 0x22, (byte) 0x33)));
    }

    @Test
    public void testCreateGtpv2HeaderWithTeid() {
        final var builder = Gtp2Header.of(Gtp2MessageType.CREATE_SESSION_REQUEST);
        final var teidBuffer = Buffers.wrap((byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44);
        builder.withTeid(Teid.of(teidBuffer));
        builder.withTlivSize(40);
        final var header = builder.build();

        assertThat(header.getVersion(), is(2));
        assertThat(header.getLength(), is(40 + 3 + 1 + 4)); // 3 for seqNo, 1 for spare and 4 for TEID
        assertThat(header.getTotalLength(), is(40 + 3 + 1 + 4 + 4)); // TEID and the 4 mandatory bytes for the header
        assertThat(header.getBuffer().capacity(), is(12)); // 12 because we have the TEID

        final var expectedTeid = Teid.of(Buffers.wrap((byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44));
        assertThat(header.getTeid(), is(Optional.of(expectedTeid)));

        final var expectedSeqNo = Buffers.wrap((byte) 0x00, (byte) 0x01, (byte) 0x02);
        assertThat(header.getSequenceNo(), is(expectedSeqNo));

        // ensure that we encode the underlying buffer correctly but re-parsing it and comparing...
        final var reParsed = Gtp2Header.frame(header.getBuffer());
        assertThat(reParsed.getVersion(), is(2));
        assertThat(reParsed.getLength(), is(40 + 3 + 1 + 4));
        assertThat(reParsed.getTotalLength(), is(40 + 3 + 1 + 4 + 4));
        assertThat(reParsed.getTeid(), is(Optional.of(expectedTeid)));
        assertThat(reParsed.getSequenceNo(), is(expectedSeqNo));
        assertThat(reParsed.getBuffer().capacity(), is(12)); // 12 because we have the TEID
    }

    @Test
    public void testCopyGtpV2Header() throws Exception {
        final var header = GtpHeader.frame(GtpRawData.deleteBearerRequestGtpv2).toGtp2Header();
        final var copy = header.copy().build();
        assertGtpRawDataDeleteBearerRequestGtpv2(copy);
        assertThat(header.getBuffer(), is(copy.getBuffer()));

        final var seqNo = Buffer.of((byte) 0x11, (byte) 0x12, (byte) 0x13);
        final var copy2 = copy.copy()
                .withSequenceNumber(seqNo)
                .withTlivSize(33)
                .build();
        assertThat(copy2.getBodyLength(), is(33));
        assertThat(copy2.getSequenceNo(), is(seqNo));
    }

    private void assertGtpRawDataDeleteBearerRequestGtpv2(final Gtp2Header header) {
        assertThat(header.getVersion(), is(2));
        assertThat(header.getLength(), is(13));
        assertThat(header.getTotalLength(), is(13 + 4));
        assertThat(header.getBodyLength(), is(13 - 8));
        assertThat(header.getMessageTypeDecimal(), is(99));
        assertThat(header.getBuffer().capacity(), is(12)); // header length is 12 because we have the TEID

        assertThat(header.getSequenceNo(), is(Buffer.of((byte) 0x35, (byte) 0x3d, (byte) 0x09)));
        assertThat(header.getSequenceNoAsDecimal(), is(3489033));

        // Values off of wireshark...
        final Teid teid = Teid.of(Buffer.of((byte) 0xa5, (byte) 0xd2, (byte) 0x68, (byte) 0xf0));
        assertThat(header.getTeid().get(), CoreMatchers.is(teid));
    }

    @Test
    public void testFrameGtpV2Header() {
        Gtp2Header header = GtpHeader.frame(GtpRawData.deleteBearerRequestGtpv2).toGtp2Header();
        assertGtpRawDataDeleteBearerRequestGtpv2(header);

        header = GtpHeader.frame(GtpRawData.deleteBearerResponseGtpv2).toGtp2Header();
        assertThat(header.getVersion(), is(2));
        assertThat(header.getLength(), is(55));
        assertThat(header.getTotalLength(), is(59));
        assertThat(header.getBodyLength(), is(59 - 12));
        assertThat(header.getMessageTypeDecimal(), is(100));
        assertThat(header.getSequenceNo(), is(Buffer.of((byte) 0x35, (byte) 0x3d, (byte) 0x09)));
        assertThat(header.getSequenceNoAsDecimal(), is(3489033));

        final var teid = Teid.of(Buffer.of((byte) 0x57, (byte) 0xb5, (byte) 0x01, (byte) 0xf8));
        assertThat(header.getTeid().get(), CoreMatchers.is(teid));
    }

    /**
     * Normally, one would use the {@link GtpHeader#frame(Buffer)} method that would
     * figure out whether it is version 1 or 2 and it would then call the correct framer.
     * However, nothing prevents a user to just call e.g {@link Gtp1Header#frame(Buffer)}
     * but pass in data for GTPv2 and then we should detect and complain.
     */
    @Test
    public void testWrongVersion() {
        ensureBlowsUp(1, 2, () -> Gtp1Header.frame(GtpRawData.deleteBearerResponseGtpv2));
        ensureBlowsUp(2, 1, () -> Gtp2Header.frame(GtpRawData.createPdpContextRequest));
    }

    private static void ensureBlowsUp(final int expected, final int actual, final Supplier<GtpHeader> fn) {
        try {
            fn.get();
            fail("Expected to blow up due to wrong GTP version");
        } catch (final GtpVersionException e) {
            assertThat(e.getExpectedVersion(), is(expected));
            assertThat(e.getActualVersion(), is(actual));
        }
    }
}
