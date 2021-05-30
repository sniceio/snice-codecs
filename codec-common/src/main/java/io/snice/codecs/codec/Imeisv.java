package io.snice.codecs.codec;

import io.snice.buffer.Buffer;
import io.snice.buffer.Buffers;

import java.util.Optional;

import static io.snice.preconditions.PreConditions.assertArgument;
import static io.snice.preconditions.PreConditions.assertNotEmpty;

/**
 * International Mobile Equipment Identity and Software Version Number (TS 23.003 section 6)
 * <p>
 * The {@link Imeisv} is an extension of the {@link Imei} and contains a two digit software version
 * as the last byte as opposed to the {@link Imei} that doesn't have a software version but may optionally
 * have a Check Digit.
 * <p>
 * By definition, the {@link Imeisv} does not have the Check Digit present but an {@link Imeisv} can easily
 * be converted by dropping the Software Version and then calculate the Check Digit.
 */
public interface Imeisv extends Imei {

    static Imeisv of(final Buffer buffer) {
        Buffers.assertNotEmpty(buffer);
        assertArgument(buffer.capacity() == 8, "The encoded IMEISV must be exactly 8 bytes long");
        final var imei = Imei.of(buffer.slice(7));
        return new DefaultImeisv(buffer, imei);
    }

    static Imeisv of(final String imeisv) {
        assertNotEmpty(imeisv, "The IMEI cannot be null or the empty String");
        assertArgument(imeisv.length() == 16, "The IMEISV must be exactly 16 digits long");
        final var buffer = Buffers.wrapAsTbcd(imeisv);
        return of(buffer);
    }

    /**
     * An {@link Imeisv} will never have the Check Digit present so this will always
     * return false.
     */
    @Override
    default boolean hasCheckDigit() {
        return false;
    }

    /**
     * Since the {@link Imeisv} will never ever have the check digit present, this will
     * always yield an {@link IllegalArgumentException}, per the contract of {@link Imei#validate()}.
     *
     * @return
     */
    @Override
    default boolean validate() {
        throw new IllegalArgumentException("An IMIESV does not ever have the Check Digit present and therefore " +
                "the correctness of the Check Digit cannot be evaluated");
    }

    @Override
    default Optional<Integer> getCheckDigit() {
        return Optional.empty();
    }

    /**
     * Asking the {@link Imeisv} to be "extended" will essentially also mean that you are converting the
     * {@link Imeisv} to an {@link Imei}. If you do not wish to convert this {@link Imeisv} to an {@link Imei}
     * that has the Check Digit present, just call the method {@link #toImei()} instead.
     *
     * @return an {@link Imei} with the Check Digit present and calculated.
     */
    @Override
    Imei extendWithCheckDigit();

    /**
     * Convert this {@link Imeisv} to a {@link Imei} without calculating the Check Digit. Since the {@link Imeisv}
     * can never ever have a Check Digit present (even a badly calculated one), the resulting {@link Imei} will
     * always be a 14 digit {@link Imei} without the Check Digit present. If you wish to have the Check Digit calculated
     * and present (so a 15 digit {@link Imei}), call the {@link #extendWithCheckDigit()} method instead.
     */
    Imei toImei();

    Buffer getSoftwareVersion();

    class DefaultImeisv implements Imeisv {

        private final Buffer buffer;
        private final Imei imei;

        private DefaultImeisv(final Buffer buffer, final Imei imei) {
            this.buffer = buffer;
            this.imei = imei;
        }

        @Override
        public Buffer getBuffer() {
            return buffer;
        }

        @Override
        public Imei extendWithCheckDigit() {
            return imei.extendWithCheckDigit();
        }

        @Override
        public Buffer getTypeAllocationCode() {
            return imei.getTypeAllocationCode();
        }

        @Override
        public Buffer getSerialNumber() {
            return imei.getSerialNumber();
        }

        @Override
        public Imei toImei() {
            return imei;
        }

        @Override
        public Buffer getSoftwareVersion() {
            return buffer.slice(7, 8);
        }
    }
}
