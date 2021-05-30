package io.snice.codecs.codec;

import io.snice.buffer.Buffer;
import io.snice.buffer.Buffers;

import java.util.Objects;

import static io.snice.preconditions.PreConditions.assertArgument;
import static io.snice.preconditions.PreConditions.assertNotEmpty;
import static io.snice.preconditions.PreConditions.assertNotNull;

public interface Imsi {

    static Imsi of(final MccMnc mccMnc, final int msin) {
        assertNotNull(mccMnc);
        assertArgument(msin >= 0 && msin < 1000000000, "The MSIN must be greater than zero and less than 1.000.000.000");
        final String msinStr = DefaultImsi.generateMsin(msin);
        final var encoded = Buffers.wrapAsTbcd(mccMnc.getAsString() + msinStr);
        return new DefaultImsi(mccMnc, msinStr, encoded);
    }

    static Imsi of(final String mccMnc, final String msin) {
        assertNotEmpty(mccMnc, "The MCC/MNC cannot be null or the empty string");
        assertNotEmpty(mccMnc, "The MSIN cannot be null or the empty string");

        // we allow the mccMnc to be separated by a slash for easier human consumption, which means
        // we need to normalize it first.
        final var mccMncProcessed = MccMnc.of(mccMnc).getAsString();
        return of(Buffers.wrapAsTbcd(mccMncProcessed + msin));
    }

    static Imsi of(final Buffer buffer) {
        final var mccMnc = MccMnc.parseAsTbcdEncoded(buffer);
        final var imsiStr = buffer.toTBCD();
        assertArgument(imsiStr.length() > mccMnc.getNoOfNibbles(), "No MSIN was specified as part of the given buffer. Only MCC/MNC was found");
        final var msin = imsiStr.substring(mccMnc.getNoOfNibbles());

        return new DefaultImsi(mccMnc, msin, buffer);
    }

    MccMnc getMccMnc();

    /**
     * Get the MSIN as a String.
     *
     * @return
     */
    String getMsin();

    /**
     * Convert the IMSI to a human readable String.
     *
     * @return
     */
    @Override
    String toString();

    /**
     * Return the raw underlying TBCD encoded {@link Buffer}.
     *
     * @return
     */
    Buffer toBuffer();

    class DefaultImsi implements Imsi {

        private static final String[] zeros = new String[]{
                "00000000",
                "0000000",
                "000000",
                "00000",
                "0000",
                "000",
                "00",
                "0",
                ""
        };

        private final MccMnc mccMnc;
        private final String msin;
        private final Buffer imsi;

        private DefaultImsi(final MccMnc mccMnc, final String msin, final Buffer imsi) {
            this.mccMnc = mccMnc;
            this.msin = msin;
            this.imsi = imsi;
        }

        private static String generateMsin(final int msin) {
            return zeros[countDigits(msin)] + msin;
        }

        /**
         * According to this https://www.baeldung.com/java-number-of-digits-in-int, the below very verbose solution is by far the fastest one
         * <p>
         * Note: this is zero based so not quite really truly counting the digits.
         */
        private static int countDigits(final int number) {
            if (number < 100000) {
                if (number < 100) {
                    if (number < 10) {
                        return 0;
                    } else {
                        return 1;
                    }
                } else {
                    if (number < 1000) {
                        return 2;
                    } else {
                        if (number < 10000) {
                            return 3;
                        } else {
                            return 4;
                        }
                    }
                }
            } else {
                if (number < 10000000) {
                    if (number < 1000000) {
                        return 5;
                    } else {
                        return 6;
                    }
                } else {
                    if (number < 100000000) {
                        return 7;
                    } else {
                        if (number < 1000000000) {
                            return 8;
                        } else {
                            return 9;
                        }
                    }
                }
            }
        }

        @Override
        public MccMnc getMccMnc() {
            return mccMnc;
        }

        @Override
        public String getMsin() {
            return msin;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final DefaultImsi that = (DefaultImsi) o;
            return imsi.equals(that.imsi);
        }

        @Override
        public int hashCode() {
            return imsi.hashCode();
        }

        @Override
        public String toString() {
            return imsi.toTBCD();
        }

        @Override
        public Buffer toBuffer() {
            return imsi;
        }
    }

}
