package io.snice.codecs.codec.gtp.gtpc.v2.type;

import io.snice.buffer.Buffer;
import io.snice.buffer.Buffers;
import io.snice.codecs.codec.MccMnc;
import io.snice.codecs.codec.gtp.gtpc.v2.type.impl.ImmutableGtpType;

import static io.snice.buffer.Buffers.assertBufferCapacity;
import static io.snice.preconditions.PreConditions.assertNotNull;

/**
 * TS 29.274 section 8.21.4
 */
public interface TaiField extends GtpType {

    static TaiField parse(final Buffer buffer) {
        assertBufferCapacity(buffer, 5, "The TAI is exactly 5 bytes long");
        final var mccMnc = MccMnc.parseAsMccMnc(buffer.slice(0, 3));
        final var tac = buffer.slice(3, 5);
        return new DefaultTeidField(buffer, mccMnc, tac);
    }

    static TaiField of(final MccMnc mccMnc, final Buffer tac) {
        assertNotNull(mccMnc, "The MccMnc cannot be null");
        assertBufferCapacity(tac, 2, "The TAC must be exactly 2 bytes long");
        return new DefaultTeidField(Buffers.wrap(mccMnc.toBuffer(), tac), mccMnc, tac);
    }


    MccMnc getMccMnc();

    Buffer getTac();

    class DefaultTeidField extends ImmutableGtpType<TaiField> implements TaiField {

        private final MccMnc mccMnc;
        private final Buffer tac;

        private DefaultTeidField(final Buffer buffer, final MccMnc mccMnc, final Buffer tac) {
            super(buffer);
            this.mccMnc = mccMnc;
            this.tac = tac;
        }

        @Override
        public MccMnc getMccMnc() {
            return mccMnc;
        }

        @Override
        public Buffer getTac() {
            return tac;
        }
    }
}
