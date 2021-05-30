package io.snice.codecs.codec;

import io.snice.buffer.Buffer;
import io.snice.buffer.Buffers;
import io.snice.buffer.WritableBuffer;
import io.snice.codecs.codec.gtp.gtpc.v1.E212;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

public class MccMncTest {

    @Test
    public void testParseFromRaw() {
        ensureMccMnc(Buffers.wrap((byte) 0x13, (byte) 0x00, (byte) 0x14), "310", "410");
        ensureMccMnc(Buffers.wrap((byte) 0x22, (byte) 0xF6, (byte) 0x30), "226", "03");
        ensureMccMnc(E212.E212_001001.getEncoded(), "001", "001");
        ensureMccMnc(E212.E212_00101.getEncoded(), "001", "01");

        ensureMccMnc(E212.E212_20650.getEncoded(), "206", "50");
        ensureMccMnc(E212.E212_21408.getEncoded(), "214", "08");
        ensureMccMnc(E212.E212_99999.getEncoded(), "999", "99");
        ensureMccMnc(E212.E212_999999.getEncoded(), "999", "999");
    }

    /**
     * We do allow for the creation of non-existent MCC/MNC combinations.
     */
    @Test
    public void testCreateNonExistentMccMnc() {
        ensureMccMnc("214/20", "214", "20");
        ensureMccMnc(E212.encodeMccMnc("214", "20"), "214", "20");

        // create a long buffer, encode the MCC/MNC into the beginning and write
        // some random stuff after. This should still be ok...
        final var buffer = WritableBuffer.of(100);
        E212.encodeMccMnc("214", "20").writeTo(buffer);
        buffer.write("hello world");
        ensureMccMnc(buffer.build(), "214", "20");
    }

    @Test
    public void testCreateMccMncEnsureToString() {
        ensureMccMncToString("310/410", "310/410");
        ensureMccMncToString("001001", "001/001");
        ensureMccMncToString("001/001", "001/001");
        ensureMccMncToString("001-001", "001/001");
        ensureMccMncToString("00101", "001/01");
        ensureMccMncToString("001/01", "001/01");
        ensureMccMncToString("001-01", "001/01");
    }

    @Test
    public void testCreateMccMnc() {
        final var b = WritableBuffer.of(3).fastForwardWriterIndex();

        // ---- First we build up 310 ---
        // 1 from 310
        b.setBit(0, 4, true);
        // these two together is 3 from 310
        b.setBit(0, 0, true);
        b.setBit(0, 1, true);
        // and the 0 from 310 is all good since it is zero already

        // ---- And now we build up 410 ---
        // mnc digit 3 is zero so don't have to touch it.
        // mnc digit 2 is 1 (from 410) so flip that bit
        b.setBit(2, 4, true);
        // mnc digit 1 is 4 (from 410) so
        // these two together is 4
        b.setBit(2, 2, true);

        final var actual = ensureMccMnc("310/410", "310", "410");
        final var expected = b.build();

        assertThat(actual, is(expected));

        ensureMccMnc("001001", "001", "001");
        ensureMccMnc("001/001", "001", "001");
        ensureMccMnc("001-001", "001", "001");

        ensureMccMnc("00101", "001", "01");
        ensureMccMnc("001/01", "001", "01");
        ensureMccMnc("001-01", "001", "01");
    }

    @Test
    public void testCreateBadMccMnc() {
        ensureParseFailure(null, "Expected to fail on a null string");
        ensureParseFailure("", "Expected to fail on an empty string");
        ensureParseFailure("0", "Expected to fail on not enough characters");
        ensureParseFailure("00", "Expected to fail on not enough characters");
        ensureParseFailure("001", "Expected to fail on not enough characters");
        ensureParseFailure("0010", "Expected to fail on not enough characters");
        ensureParseFailure("0010001", "Expected to fail on too many characters");
        ensureParseFailure("00100001", "Expected to fail on too many characters");

        // not a legal separator
        ensureParseFailure("001a0001", "Expected to fail on too many characters");
        ensureParseFailure("001a001", "Expected to fail on too many characters");
        ensureParseFailure("001a01", "Expected to fail on too many characters");
        ensureParseFailure("001a1", "Expected to fail on too many characters");

        // not digits in all places...
        ensureParseFailure("00100a", "Expected to fail on not digits everywhere");
        ensureParseFailure("0010a0", "Expected to fail on not digits everywhere");
        ensureParseFailure("001a00", "Expected to fail on not digits everywhere");
        ensureParseFailure("00a100", "Expected to fail on not digits everywhere");
        ensureParseFailure("0a0100", "Expected to fail on not digits everywhere");
        ensureParseFailure("a00100", "Expected to fail on not digits everywhere");

        ensureParseFailure("001/00a", "Expected to fail on not digits everywhere");
        ensureParseFailure("001/0a0", "Expected to fail on not digits everywhere");
        ensureParseFailure("001/a00", "Expected to fail on not digits everywhere");
        ensureParseFailure("00a/100", "Expected to fail on not digits everywhere");
        ensureParseFailure("0a0/100", "Expected to fail on not digits everywhere");
        ensureParseFailure("a00/100", "Expected to fail on not digits everywhere");

        ensureParseFailure("0010a", "Expected to fail on not digits everywhere");
        ensureParseFailure("0010a", "Expected to fail on not digits everywhere");
        ensureParseFailure("001a0", "Expected to fail on not digits everywhere");
        ensureParseFailure("00a10", "Expected to fail on not digits everywhere");
        ensureParseFailure("0a010", "Expected to fail on not digits everywhere");
        ensureParseFailure("a0010", "Expected to fail on not digits everywhere");

        ensureParseFailure("001/0a", "Expected to fail on not digits everywhere");
        ensureParseFailure("001/0a", "Expected to fail on not digits everywhere");
        ensureParseFailure("001/a0", "Expected to fail on not digits everywhere");
        ensureParseFailure("00a/10", "Expected to fail on not digits everywhere");
        ensureParseFailure("0a0/10", "Expected to fail on not digits everywhere");
        ensureParseFailure("a00/10", "Expected to fail on not digits everywhere");
    }

    private void ensureParseFailure(final String input, final String msg) {
        try {
            MccMnc.of(input);
            fail(msg);
        } catch (final IllegalArgumentException e) {
            // expected
        }
    }

    private Buffer ensureMccMnc(final Buffer buffer, final String expectedMcc, final String expectedMnc) {
        final var mccMnc = MccMnc.parseAsMccMnc(buffer);
        assertThat(mccMnc.getMcc(), is(expectedMcc));
        assertThat(mccMnc.getMnc(), is(expectedMnc));
        return mccMnc.toBuffer();
    }


    private Buffer ensureMccMnc(final String input, final String expectedMcc, final String expectedMnc) {
        final var mccMnc = MccMnc.of(input);
        assertThat(mccMnc.getMcc(), is(expectedMcc));
        assertThat(mccMnc.getMnc(), is(expectedMnc));
        return mccMnc.toBuffer();
    }

    private void ensureMccMncToString(final String input, final String expected) {
        final var mccMnc = MccMnc.of(input);
        assertThat(mccMnc.toString(), is(expected));
    }

}
