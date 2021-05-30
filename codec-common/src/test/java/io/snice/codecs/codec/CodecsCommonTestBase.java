package io.snice.codecs.codec;

import io.snice.buffer.Buffers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CodecsCommonTestBase {

    public static void ensureImei(final String str, final int expectedCheckDigit) {
        final var imei = Imei.of(Buffers.wrapAsTbcd(str));
        ensureImei(imei, expectedCheckDigit);
    }

    public static void ensureImei(final Imei imei, final int expectedCheckDigit) {
        assertThat(Imei.calculateCheckDigit(imei.getBuffer()), is(expectedCheckDigit));
        assertThat(imei.hasCheckDigit(), is(true));
        assertThat(imei.validate(), is(true));
        assertThat(imei.getCheckDigit().get(), is(expectedCheckDigit));
    }

    public static void ensureExtend(final String str, final int expectedCheckDigit) {
        final var imei = Imei.of(Buffers.wrapAsTbcd(str)).extendWithCheckDigit();
        ensureImei(imei, expectedCheckDigit);
    }

    public static void ensureImei(final String imei) {
        assertThat(Imei.of(imei).toString(), is(imei));
    }

    public static void ensureTac(final String imei, final String expected) {
        assertThat(Imei.of(imei).getTypeAllocationCode(), is(Buffers.wrapAsTbcd(expected)));
    }

    public static void ensureImeiSvTac(final String value, final String expected) {
        final var imiesv = Imeisv.of(value);
        assertThat(imiesv.getTypeAllocationCode(), is(Buffers.wrapAsTbcd(expected)));
        assertThat(imiesv.toImei().getTypeAllocationCode(), is(Buffers.wrapAsTbcd(expected)));
    }

    public static void ensureSnr(final String imei, final String expected) {
        assertThat(Imei.of(imei).getSerialNumber(), is(Buffers.wrapAsTbcd(expected)));
    }

    public static void ensureImeiSvSnr(final String value, final String expected) {
        final var imiesv = Imeisv.of(value);
        assertThat(imiesv.getSerialNumber(), is(Buffers.wrapAsTbcd(expected)));
        assertThat(imiesv.toImei().getSerialNumber(), is(Buffers.wrapAsTbcd(expected)));
    }

    public static void ensureImeiSvSoftwareVersion(final String imeisv, final String expected) {
        assertThat(Imeisv.of(imeisv).getSoftwareVersion(), is(Buffers.wrapAsTbcd(expected)));
    }


}
