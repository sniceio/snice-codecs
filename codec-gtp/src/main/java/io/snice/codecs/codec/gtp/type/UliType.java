package io.snice.codecs.codec.gtp.type;

import io.snice.buffer.Buffer;
import io.snice.buffer.Buffers;
import io.snice.buffer.WritableBuffer;
import io.snice.codecs.codec.gtp.type.impl.ImmutableGtpType;

import java.util.Optional;

import static io.snice.buffer.Buffers.assertBufferCapacityAtLeast;
import static io.snice.preconditions.PreConditions.assertNotNull;
import static io.snice.preconditions.PreConditions.assertNull;

public interface UliType extends GtpType {

    static UliType parse(final Buffer buffer) {
        assertBufferCapacityAtLeast(buffer, 1, "The ULI must be at least 1 byte long");
        final var readable = buffer.toReadableBuffer();
        readable.readByte(); // header, skip...

        TaiField tai = null;
        EcgiField eci = null;
        if (buffer.getBit0(0)) {
            readable.readBytes(7); // CGI - haven't implemented just yet
        }

        if (buffer.getBit1(0)) {
            readable.readBytes(7); // SAI - haven't implemented just yet
        }

        if (buffer.getBit2(0)) {
            readable.readBytes(7); // RAI - haven't implemented just yet
        }

        // TAI
        if (buffer.getBit3(0)) {
            tai = TaiField.parse(readable.readBytes(5));
        }

        // ECGI
        if (buffer.getBit4(0)) {
            eci = EcgiField.parse(readable.readBytes(7));
        }

        if (buffer.getBit5(0)) {
            readable.readBytes(5); // LAI - haven't implemented just yet
        }

        final var uli = buffer.slice(readable.getReaderIndex());
        return new DefaultUliType(uli, tai, eci);
    }

    static UliType parse(final String buffer) {
        return parse(Buffers.wrap(buffer));
    }

    static UliType ofValue(final Buffer buffer) {
        return parse(buffer);
    }

    static UliType ofValue(final String buffer) {
        return parse(buffer);
    }

    static Builder create() {
        return new Builder();
    }

    Optional<TaiField> getTai();

    Optional<EcgiField> getEcgi();

    class Builder {

        /**
         * The size of the final buffer. The initial 1 is for the first byte
         * with all the flags in the ULI.
         */
        private int size = 1;

        private TaiField tai;

        private EcgiField ecgi;

        private Builder() {
            // left empty intentionally
        }

        public Builder withTai(final TaiField tai) {
            assertNotNull(tai);
            assertNull(this.tai, "TAI has already been specified");
            this.tai = tai;
            size += 5;
            return this;
        }

        public Builder withEcgi(final EcgiField ecgi) {
            assertNotNull(ecgi);
            assertNull(this.ecgi, "ECGI has already been specified");
            this.ecgi = ecgi;
            size += 7;
            return this;
        }

        public UliType build() {
            final var writable = WritableBuffer.of(size);
            writable.setWriterIndex(1);

            if (tai != null) {
                writable.setBit3(0, true);
                tai.getBuffer().writeTo(writable);
            }

            if (ecgi != null) {
                writable.setBit4(0, true);
                ecgi.getBuffer().writeTo(writable);
            }

            return new DefaultUliType(writable.build(), tai, ecgi);
        }
    }

    class DefaultUliType extends ImmutableGtpType<UliType> implements UliType {

        // According to TS 29.274 section 8.21,
        //
        // The ULI IE shall contain only one identity of the same type (e.g. more than one CGI cannot be included), but ULI IE
        // may contain more than one identity of a different type (e.g. ECGI and TAI). The flags LAI, ECGI, TAI, RAI, SAI and
        // CGI in octet 5 indicate if the corresponding type shall be present in a respective field or not. If one of these flags is set
        // to "0", the corresponding field shall not be present at all. If more than one identity of different type is present, then they
        // shall be sorted in the following order: CGI, SAI, RAI, TAI, ECGI, LAI.

        private final Optional<TaiField> tai;
        private final Optional<EcgiField> ecgi;

        private DefaultUliType(final Buffer buffer, final TaiField tai, final EcgiField ecgi) {
            super(buffer);
            this.tai = Optional.ofNullable(tai);
            this.ecgi = Optional.ofNullable(ecgi);
        }

        @Override
        public Optional<TaiField> getTai() {
            return tai;
        }

        @Override
        public Optional<EcgiField> getEcgi() {
            return ecgi;
        }
    }

}
