package io.snice.codecs.codec;

import io.snice.buffer.Buffer;
import io.snice.buffer.Buffers;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import static io.snice.preconditions.PreConditions.assertArgument;
import static io.snice.preconditions.PreConditions.assertNotEmpty;

/**
 * International Mobile Equipment Identity (TS 23.003 section 6)
 * <p>
 * Note that this {@link Imei} does not make any guarantees that the Check Digit is present nor, it is
 * correct if it does indeed exists. If you want to ensure that the Check Digit is correct, then call
 * the {@link Imei#validate()} method. Also, since an IMEI does not actually need to have the Check Digit
 * present, you may ask to "extend" it by calling {@link Imei#extendWithCheckDigit()}.
 */
public interface Imei {

    /**
     * Create an {@link Imei} from a {@link Buffer}.
     * You would use this method when you read a raw byte-array off of e.g. the network.
     *
     * @throws IllegalArgumentException in case the buffer is empty or not 7 or 8 bytes long.
     */
    static Imei of(final Buffer imei) {
        Buffers.assertNotEmpty(imei);
        assertArgument(imei.capacity() == 7 || imei.capacity() == 8, "The encoded IMEI must be exactly 7 or 8 bytes long");

        // if 8 bytes, then bits 4-7 of the last byte must be set to 1111.
        if (imei.capacity() == 8) {
            assertArgument((imei.getByte(7) & 0xF0) == 0xF0, "The IMEI can only be 15 digits long, this one has 16 digits");
        }
        return new DefaultImei(imei);
    }

    /**
     * Create a new IMEI from a human readable string.
     *
     * @throws IllegalArgumentException in case the given string is not exactly 14 or 15 digits long.
     */
    static Imei of(final String imei) {
        assertNotEmpty(imei, "The IMEI cannot be null or the empty String");
        assertArgument(imei.length() == 14 || imei.length() == 15, "The IMEI must be exactly 14 or 15 digits long");
        final var buffer = Buffers.wrapAsTbcd(imei);
        return new DefaultImei(buffer);
    }

    /**
     * Note: constructing an IMEI from a long is not something you would typically do since
     * an IMEI can start with leading zeros, which obviously would not be expressed in the long.
     * The main purpose of this version, and the patching of the long to a full 14 digit IMEI,
     * is for testing, and in particular, performance testing where you want to generate
     * many random IMEIs. Look at the Snice Gatling GTP plugin where this is being used.
     *
     * For production, you probably don't want to use this.
     *
     * @param value
     * @return
     */
    static Imei of(final long value) {
        final var str = Long.toString(Math.abs(value));
        final var diff = 14 - str.length();
        if (diff > 0) {
            if (diff == 1) {
                return of("1" + str);
            }

            if (diff == 2) {
                return of("22" + str);
            }

            final var builder = new StringBuilder();
            for (int i = 0; i < diff; ++i) {
                builder.append("3");
            }
            return of(builder.append(str).toString());
        }
        return of(str);
    }

    /**
     * Just a completely random IMEI, which will not be correct so mainly for testing etc.
     *
     * @return
     */
    static Imei random() {
        final var random = new Random();
        return of(random.nextLong() % 100000000000000L);
    }

    /**
     * Check if the this IMEI has check digit present.
     *
     * @return
     */
    boolean hasCheckDigit();

    /**
     * Get the underlying raw {@link Buffer}. This buffer is encoded correctly and
     * can therefore be written out to a raw network stream for those protocols (such as GTP)
     * where it should be transmitted as a TBCD.
     */
    Buffer getBuffer();

    /**
     * Validate the IMEI by ensuring that the Check Digit is accurate.
     *
     * @return true if the check digit is correct, false otherwise.
     * @throws IllegalArgumentException in case this {@link Imei} doesn't have a check digit present.
     */
    boolean validate();

    /**
     * An IMEI may not have the check digit set so if that is the case, you may ask
     * to calculate the correct check digit and "extend" the current IMEI. Since
     * the {@link Imei} is an immutable object, a new {@link Imei} will be constructed
     * if the current one doesn't actually have a check digit present (or if that digit
     * isn't correct).
     *
     * @return an {@link Imei} with a correct Check Digit present.
     */
    Imei extendWithCheckDigit();

    /**
     * Get the Type Allocation Code (TAC), which are the first 8 digits of the {@link Imei}.
     */
    Buffer getTypeAllocationCode();

    /**
     * Get the Serial Number (SNR), which are 6 digits long
     */
    Buffer getSerialNumber();

    /**
     * Get the check digit, if present.
     */
    Optional<Integer> getCheckDigit();

    /**
     * Calculate the check digit according TS 23.003 Annex B (Luhn Check Digit)
     * <p>
     * Note that this method does not check that the given buffer is a valid
     * IMEI. It does not even check if the buffer is null or empty since the
     * intention of this method is to be called in a context where this can be
     * ensured. As such, it is also simply assumed that the buffer has 7 bytes
     * present since an IMEI needs to have 14 digits...
     */
    static int calculateCheckDigit(final Buffer buffer) {
        int sum = 0;
        for (int i = 0; i < 7; ++i) {
            final var b = buffer.getByte(i);
            sum += (b & 0x0F);
            final int v = 2 * ((b & 0xF0) >> 4);
            sum += v / 10;
            sum += v % 10;
        }

        if (sum % 10 == 0) {
            return 0;
        }

        return (sum / 10 + 1) * 10 - sum;
    }

    class DefaultImei implements Imei {
        private final Buffer buffer;

        private DefaultImei(final Buffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public String toString() {
            return buffer.toTBCD();
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final DefaultImei that = (DefaultImei) o;
            return buffer.equals(that.buffer);
        }

        @Override
        public int hashCode() {
            return Objects.hash(buffer);
        }

        @Override
        public boolean hasCheckDigit() {
            return buffer.capacity() == 8;
        }

        @Override
        public Buffer getBuffer() {
            return buffer;
        }

        @Override
        public boolean validate() {
            if (!hasCheckDigit()) {
                throw new IllegalArgumentException("This IMEI doesn't have a Check Digit present and therefore, " +
                        "the correctness of the Check Digit cannot be evaluated");
            }
            final int expected = calculateCheckDigit(buffer);
            final int actual = buffer.getByte(7) & 0x0F;
            return actual == expected;
        }

        @Override
        public Imei extendWithCheckDigit() {
            if (hasCheckDigit() && validate()) {
                return this;
            }

            final int checkDigit = calculateCheckDigit(buffer);
            final byte last = (byte) ((byte) 0xF0 + (byte) (checkDigit & 0x0F));
            return new DefaultImei(Buffers.wrap(buffer.slice(7), last));
        }

        @Override
        public Buffer getTypeAllocationCode() {
            return buffer.slice(4);
        }

        @Override
        public Buffer getSerialNumber() {
            return buffer.slice(4, 7);
        }

        @Override
        public Optional<Integer> getCheckDigit() {
            if (!hasCheckDigit()) {
                return Optional.empty();
            }

            return Optional.of(buffer.getByte(7) & 0x0F);
        }

    }

}
