package io.snice.codecs.codec;

import io.snice.buffer.Buffer;
import io.snice.buffer.WritableBuffer;
import io.snice.codecs.codec.gtp.gtpc.v1.E212;

import static io.snice.buffer.Buffers.assertBufferCapacityAtLeast;
import static io.snice.preconditions.PreConditions.assertArgument;
import static io.snice.preconditions.PreConditions.assertNotEmpty;

/**
 * Represents an MCC/MNC, which can be encoded/decoded in two different ways.
 * If the MCC/MNC is encoded within an IMSI, it is just encoded as a "standard" TBCD
 * sequence but if it is encoded "standalone" as part of e.g. a GTP IE (e.g. IE Type 83 = Serving Network )
 * then it is encoded differently.
 */
public interface MccMnc {

    /**
     * Parse the given {@link Buffer} as a MccMnc and it is assumed that the buffer
     * is encoded as a "mcc/mnc" pattern as in:
     *
     * <pre>
     *     8   7   6   5   4   3   2   1
     *   +---+---+---+---+---+---+---+---+
     *   |  MCC digit 2  |  MCC digit 1  |  octet x
     *   +---------------+---------------+
     *   |  MNC digit 3  |  MCC digit 3  |  octet x+1
     *   +---------------+---------------+
     *   | MNC digit 2   |  MNC digit 1  |  octet x+2
     *   +---------------+---------------+
     * </pre>
     * <p>
     * When the MCC/MNC is encded according to this pattern, as it typically is when it is
     * e.g. part of a GTP IE, we do not have to guess if the MNC is two of three digits since
     * the 3rd MNC digit is set to F (1111 binary, 15 decimal) if it is not present, unlike the
     * TBCD encoded version where we don't know this. See {@link #parseAsTbcdEncoded(Buffer)}.
     *
     * @param buffer
     * @return
     */
    static MccMnc parseAsMccMnc(final Buffer buffer) {
        assertBufferCapacityAtLeast(buffer, 3, "The MccMnc is encoded within 3 bytes. This buffer is shorter.");
        final var mccMnc = buffer.slice(3);
        final var byte1 = mccMnc.getByte(0);
        final var byte2 = mccMnc.getByte(1);
        final var byte3 = mccMnc.getByte(2);

        final int mccDigit1 = byte1 & 0x0F;
        final int mccDigit2 = (byte1 & 0xF0) >> 4;
        final int mccDigit3 = byte2 & 0x0F;

        final int mncDigit3 = (byte2 & 0xF0) >> 4;
        final int mncDigit2 = (byte3 & 0xF0) >> 4;
        final int mncDigit1 = byte3 & 0x0F;

        final int mcc = mccDigit1 * 100 + mccDigit2 * 10 + mccDigit3;

        final int mccMncValue = mncDigit3 == 15 ? mcc * 100 + mncDigit1 * 10 + mncDigit2 : mcc * 1000 + mncDigit1 * 100 + mncDigit2 * 10 + mncDigit3;

        final var e212 = E212.lookup(mccMncValue);
        if (e212 != null) {
            return new DefaultMccMnc(e212);
        }

        final String mccStr = String.format("%d%d%d", mccDigit1, mccDigit2, mccDigit3);
        final String mncStr = mncDigit3 == 15 ? String.format("%d%d", mncDigit1, mncDigit2) : String.format("%d%d%d", mncDigit1, mncDigit2, mncDigit3);
        return new UnknownMccMnc(mccStr, mncStr, mccMnc);
    }

    /**
     * Parse the given {@link Buffer} as a MccMnc and it is assumed that the buffer
     * is TBCD encoded. Typically, the MCC/MNC is TBCD encoded when it is part of
     * an {@link Imsi} and in that case, we also have a hard time figuring out
     * whether or not the MNC is of two or three digits.
     * <p>
     * Note that when the MCC/MNC is "mcc/mnc" encoded, we do not have this issue.
     * See {@link #parseAsMccMnc(Buffer)}.
     *
     * @param buffer
     * @return
     */
    static MccMnc parseAsTbcdEncoded(final Buffer buffer) {
        assertBufferCapacityAtLeast(buffer, 3, "The MccMnc is encoded within 3 bytes. This buffer is shorter.");
        final var mccMnc = buffer.slice(3);
        final var mccMncStr = mccMnc.toTBCD();

        // first try and see if there is a 2-digit MNC match and if so, assume
        // this is correct. If not, assume a 3-digit MNC.
        final var mccMnc2Digits = Integer.parseInt(mccMncStr.substring(0, 5));
        final var e212TwoDigits = E212.lookup(mccMnc2Digits);
        if (e212TwoDigits != null) {
            return new DefaultMccMnc(e212TwoDigits);
        }

        final var e212ThreeDigits = E212.lookup(Integer.parseInt(mccMncStr));
        if (e212ThreeDigits != null) {
            return new DefaultMccMnc(e212ThreeDigits);
        }

        // no known match, assume a MNC length of 3...
        final String mccStr = mccMncStr.substring(0, 3);
        final String mncStr = mccMncStr.substring(3);
        return new UnknownMccMnc(mccStr, mncStr, mccMnc);
    }

    /**
     * Create a MCC/MNC by parsing the given string. It is assumed that the MCC is
     * 3 digits long followed by a 2 or 3 digit MNC. You can separate the two fields
     * by a slash '/' for readability and we'll parse it as such.
     * <p>
     * See 3gpp TS 24.008 release 13 section 10.5.1.13  PLMN List for the full specification.
     * One thing to point out, leading zeros matters. As such '001' and '01' are actually two
     * different things but represented as an integer, they obviously wouldn't be.
     *
     * @param mccMnc
     * @return
     */
    static MccMnc of(final String mccMnc) {
        assertNotEmpty(mccMnc, "The MCC/MNC string cannot be null or the empty string");
        assertArgument(mccMnc.length() >= 5 && mccMnc.length() <= 7, "The MCC/MNC string must be between 5 and 7 character long");
        final var mcc = mccMnc.substring(0, 3);
        final var mnc = mccMnc.substring(3 + (isSeparator(mccMnc.charAt(3)) ? 1 : 0));
        assertArgument(mnc.length() <= 3, "Expected a Separator character to be present");
        return of(mcc, mnc);
    }

    static MccMnc of(final String mcc, final String mnc) throws IllegalArgumentException {
        try {
            final var e212 = E212.lookup(Integer.parseInt(mcc + mnc));
            if (e212 != null) {
                return new DefaultMccMnc(e212);
            }

            final var encoded = E212.encodeMccMnc(mcc, mnc);
            return new UnknownMccMnc(mcc, mnc, encoded);
        } catch (final NumberFormatException e) {
            throw new IllegalArgumentException("Expected all of the characters in the MCC/MNC to be digits");
        }
    }

    private static boolean isSeparator(final Character ch) {
        return ch == '/' || ch == '-';
    }

    /**
     * Convert this {@link MccMnc} to a binary representation that you can use to write
     * to the network (will be encoded in TBCD format)
     *
     * @return
     */
    Buffer toBuffer();

    /**
     * Return the MCC/MNC as a string of format <mccmnc>, unlike the {@link #toString()}
     * that returns a more human readable string where the MCC & MNC are separated by a '/'.
     */
    String getAsString();

    String getMcc();

    String getMnc();

    /**
     * An MCC/MNC is either 2.5 or 3 octects long and when encoding the MCC/MNC into e.g. an {@link Imsi}
     * we need to know which one it is. Therefore, this method returns the number of nibbles that this
     * MCC/MNC is occupying.
     */
    int getNoOfNibbles();

    /**
     * Write this {@link MccMnc} to the supplied {@link WritableBuffer} and return how many nibbles
     * that was written.
     *
     * @param buffer
     * @return
     */
    int writeTo(WritableBuffer buffer);

    default int getMncLength() {
        return getMnc().length();
    }

    class UnknownMccMnc implements MccMnc {
        private final Buffer encoded;
        private final String mcc;
        private final String mnc;

        private UnknownMccMnc(final String mcc, final String mnc, final Buffer encoded) {
            this.encoded = encoded;
            this.mcc = mcc;
            this.mnc = mnc;
        }

        @Override
        public String getAsString() {
            return mcc + mnc;
        }

        @Override
        public Buffer toBuffer() {
            return encoded;
        }

        @Override
        public String getMcc() {
            return mcc;
        }

        @Override
        public String getMnc() {
            return mnc;
        }

        @Override
        public int getNoOfNibbles() {
            return 3 + mnc.length();
        }

        @Override
        public int writeTo(final WritableBuffer buffer) {
            encoded.writeTo(buffer);
            return getNoOfNibbles();
        }

        @Override
        public int hashCode() {
            return encoded.hashCode();
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final UnknownMccMnc that = (UnknownMccMnc) o;
            return encoded.equals(that.encoded);
        }

        @Override
        public String toString() {
            return "Unknown(" + mcc + "/" + mnc + ")";
        }
    }

    class DefaultMccMnc implements MccMnc {

        private final E212 e212;

        private DefaultMccMnc(final E212 e212) {
            this.e212 = e212;
        }

        @Override
        public Buffer toBuffer() {
            return e212.getEncoded();
        }

        @Override
        public String getMcc() {
            return e212.getMcc();
        }

        @Override
        public String getMnc() {
            return e212.getMnc();
        }

        @Override
        public int getNoOfNibbles() {
            return e212.getNoOfNibbles();
        }

        @Override
        public int writeTo(final WritableBuffer buffer) {
            e212.getEncoded().writeTo(buffer);
            return getNoOfNibbles();
        }


        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final DefaultMccMnc that = (DefaultMccMnc) o;
            return e212 == that.e212;
        }

        @Override
        public int hashCode() {
            return e212.hashCode();
        }

        @Override
        public String getAsString() {
            return e212.getAsString();
        }

        @Override
        public String toString() {
            return e212.getMcc() + "/" + e212.getMnc();
        }
    }
}
