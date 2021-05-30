package io.snice.codecs.codec;

import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

public class ImeisvTest extends CodecsCommonTestBase {


    @Test
    public void testGetTypeAllocationCode() {
        ensureImeiSvTac("2605317931138371", "26053179");
        ensureImeiSvTac("8900100402807052", "89001004");
        ensureImeiSvTac("3525360900003963", "35253609");
        ensureImeiSvTac("4901542032375184", "49015420");
        ensureImeiSvTac("2901542032375105", "29015420");
    }

    @Test
    public void testSerialNumber() {
        ensureImeiSvSnr("2605317931138375", "311383");
        ensureImeiSvSnr("8900100402807055", "028070");
        ensureImeiSvSnr("3525360900003965", "000039");
        ensureImeiSvSnr("4901542032375185", "323751");
        ensureImeiSvSnr("2901542032375105", "323751");
    }

    @Test
    public void testSoftwareVersion() {
        ensureImeiSvSoftwareVersion("2605317931138375", "75");
        ensureImeiSvSoftwareVersion("8900100402807055", "55");
        ensureImeiSvSoftwareVersion("3525360900003965", "65");
        ensureImeiSvSoftwareVersion("4901542032375185", "85");
        ensureImeiSvSoftwareVersion("2901542032375105", "05");
    }

    @Test
    public void testConvertToImei() {
        ensureImei(Imeisv.of("2605317931138371").extendWithCheckDigit(), 7);
        ensureImei(Imeisv.of("8900100402807051").extendWithCheckDigit(), 5);
        ensureImei(Imeisv.of("3525360900003961").extendWithCheckDigit(), 6);
        ensureImei(Imeisv.of("4901542032375181").extendWithCheckDigit(), 8);
        ensureImei(Imeisv.of("2901542032375101").extendWithCheckDigit(), 0);
    }

    /**
     * An IMEISV never has a check digit so make sure of that...
     */
    @Test
    public void testNoCheckDigit() {
        final var imeisv = Imeisv.of("2605317931138375");
        assertThat(imeisv.hasCheckDigit(), is(false));
        assertThat(imeisv.getCheckDigit(), is(Optional.empty()));
        try {
            imeisv.validate();
            fail("Validating an IMIESV should never be doable since an IMIESV will never ever have a check digit present");
        } catch (final IllegalArgumentException e) {
            // expected
        }
    }
}
