package io.snice.codecs.codec;

import static io.snice.preconditions.PreConditions.assertArgument;

public interface Iccid {

    /**
     * Generate a 20 digit ICCID by padding the given count to 20 digits.
     *
     * @param iccid
     * @return
     */
    static Iccid of(final long iccid) {
        assertArgument(iccid >= 0, "The ICCID must be greater than zero");
        final String asString = Long.toString(iccid); // TODO: convert this like the IMSI
        final String padded = DefaultIccid.zeros[asString.length() - 1] + asString;
        return new DefaultIccid(padded);
    }

    class DefaultIccid implements Iccid {

        private static final String[] zeros = new String[]{
                "0000000000000000000",
                "000000000000000000",
                "00000000000000000",
                "0000000000000000",
                "000000000000000",
                "00000000000000",
                "0000000000000",
                "000000000000",
                "00000000000",
                "0000000000",
                "000000000",
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

        private final String iccid;

        private DefaultIccid(final String iccid) {
            this.iccid = iccid;
        }

        @Override
        public String toString() {
            return iccid;
        }
    }
}
