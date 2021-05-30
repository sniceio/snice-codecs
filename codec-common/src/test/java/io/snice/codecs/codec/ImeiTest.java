package io.snice.codecs.codec;

import io.snice.buffer.Buffer;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

public class ImeiTest extends CodecsCommonTestBase {

    @Test
    public void testCreateImei() {
        ensureImei("12345678901234");
        ensureImei("123456789012345");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTooShortImei() {
        Imei.of("1234567890123");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTooLongImei() {
        Imei.of("1234567890123450");
    }

    @Test
    public void testFrameFrom14TbcdBuffer() {
        // copied from wireshark. It's a fake IMEI but ensured that Wireshark agreed with the TBCD encoding
        final var buffer = Buffer.of((byte) 0x21, (byte) 0x43, (byte) 0x65, (byte) 0x87, (byte) 0x09, (byte) 0x11, (byte) 0x31);
        final var imei = Imei.of(buffer.toTBCD());
        final var imei2 = Imei.of(buffer);
        assertThat(imei.toString(), is("12345678901113"));
        assertThat(imei2.toString(), is("12345678901113"));
        assertThat(imei, is(imei2));
    }

    /**
     * Ensure we calculate the check digit correctly. The below example is taken from TS 23.003 Annex B
     */
    @Test
    public void testCalculateCheckDigit() {
        ensureImei("260531793113837", 7);
        ensureImei("890010040280705", 5);
        ensureImei("352536090000396", 6);

        // example from wikipedia
        ensureImei("490154203237518", 8);

        // and test with one that generates zero
        // since that "calculation" is slightly different
        ensureImei("290154203237510", 0);
    }

    @Test
    public void testGetTypeAllocationCode() {
        ensureTac("260531793113837", "26053179");
        ensureTac("890010040280705", "89001004");
        ensureTac("352536090000396", "35253609");
        ensureTac("490154203237518", "49015420");
        ensureTac("290154203237510", "29015420");
    }

    @Test
    public void testSerialNumber() {
        ensureSnr("260531793113837", "311383");
        ensureSnr("890010040280705", "028070");
        ensureSnr("352536090000396", "000039");
        ensureSnr("490154203237518", "323751");
        ensureSnr("290154203237510", "323751");
    }

    /**
     * If an IMEI is missing the check digit, or if that digit is wrong, we can
     * ask to generate a new correct IMEI.
     */
    @Test
    public void testToExtendImeiWithCheckDigit() {

        // IMEIs with missing check digit
        ensureExtend("26053179311383", 7);
        ensureExtend("89001004028070", 5);
        ensureExtend("35253609000039", 6);
        ensureExtend("49015420323751", 8);

        // same as above but with the wrong digit, which means
        // that we should re-calculate it and then return a new
        // IMEI with the correct digit set.
        ensureExtend("260531793113831", 7);
        ensureExtend("890010040280701", 5);
        ensureExtend("352536090000391", 6);
        ensureExtend("490154203237511", 8);
    }

    /**
     * This is really more of a test of TCBD encoding but this is a 15 digit IMEI which means that bits 4-7 of the last
     * byte is set to all 1s (1111).
     */
    @Test
    public void testFrameFrom15DigitsTbcdBuffer() {
        final var buffer = Buffer.of((byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55, (byte) 0x66, (byte) 0x77, (byte) 0xf8);
        final var imei = Imei.of(buffer.toTBCD());
        final var imei2 = Imei.of(buffer);
        assertThat(imei.toString(), is("112233445566778"));
        assertThat(imei2.toString(), is("112233445566778"));
        assertThat(imei, is(imei2));
    }

    /**
     * In TBCD, setting all bits 4-7 in the last byte indicates whether the digit is "empty/not set". As such, anything but
     * all 1s at those position indicates that there is a digit there, which isn't allowed for an IMEI.
     */
    @Test
    public void testFrameFrom16DigitsBuffer() {

        // a bit overkill but we are testing every combination...
        for (int i = 0; i < 16; ++i) {
            for (int k = 0; k < 15; ++k) {
                final var b0 = i & 0x0F;
                final var a0 = k << 4;
                final var b = (byte) (a0 + b0);
                final var buffer = Buffer.of((byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55, (byte) 0x66, (byte) 0x77, b);
                ensureIllegalImei(buffer);
            }
        }
    }

    private void ensureIllegalImei(final Buffer buffer) {
        try {
            Imei.of(buffer);
            fail("Expected to blow up here");
        } catch (final IllegalArgumentException e) {
            // also make sure the message is correct.
            assertThat(e.getMessage(), is("The IMEI can only be 15 digits long, this one has 16 digits"));
        }
    }

    @Test
    public void testCreateRandomImei() {
        assertThat(Imei.random().extendWithCheckDigit().validate(), is(true));
    }

    @Test
    public void testCreateFromLong() {
        assertThat(Imei.of(1L).extendWithCheckDigit().validate(), is(true));
        assertThat(Imei.of(2L).extendWithCheckDigit().validate(), is(true));
        assertThat(Imei.of(10L).extendWithCheckDigit().validate(), is(true));
        assertThat(Imei.of(123456789012L).extendWithCheckDigit().validate(), is(true));
        assertThat(Imei.of(1234567890123L).extendWithCheckDigit().validate(), is(true));
        assertThat(Imei.of(12345678901234L).extendWithCheckDigit().validate(), is(true));
        assertThat(Imei.of(123456789012345L).extendWithCheckDigit().validate(), is(true));
    }


}