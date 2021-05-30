package io.snice.codecs.codec.gtp.gtpc.v2.type;

import io.snice.buffer.Buffer;
import io.snice.buffer.Buffers;
import io.snice.codecs.codec.MccMnc;
import io.snice.codecs.codec.gtp.gtpc.v2.type.impl.ImmutableGtpType;

import static io.snice.buffer.Buffers.assertBufferCapacity;
import static io.snice.preconditions.PreConditions.assertNotNull;

/**
 * TS 29.274 section 8.21.5
 */
public interface EcgiField extends GtpType {

    static EcgiField parse(final Buffer buffer) {
        assertBufferCapacity(buffer, 7, "The ECGI is exactly 7 bytes long");
        final var mccMnc = MccMnc.parseAsMccMnc(buffer.slice(0, 3));
        final var eci = buffer.slice(3, 7);
        return new DefaultEcgiField(buffer, mccMnc, eci);
    }

    static EcgiField of(final MccMnc mccMnc, final Buffer eci) {
        assertNotNull(mccMnc, "The MccMnc cannot be null");
        assertBufferCapacity(eci, 4, "The ECGI must be exactly 4 bytes long");
        return new DefaultEcgiField(Buffers.wrap(mccMnc.toBuffer(), eci), mccMnc, eci);
    }


    MccMnc getMccMnc();

    Buffer getEci();

    class DefaultEcgiField extends ImmutableGtpType<EcgiField> implements EcgiField {

        private final MccMnc mccMnc;
        private final Buffer eci;

        private DefaultEcgiField(final Buffer buffer, final MccMnc mccMnc, final Buffer eci) {
            super(buffer);
            this.mccMnc = mccMnc;
            this.eci = eci;
        }

        @Override
        public MccMnc getMccMnc() {
            return mccMnc;
        }

        @Override
        public Buffer getEci() {
            return eci;
        }
    }
}
