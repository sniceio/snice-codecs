package io.snice.codecs.codec.gtp.type;

import io.snice.buffer.Buffer;
import io.snice.codecs.codec.MccMnc;
import io.snice.codecs.codec.gtp.type.impl.ImmutableGtpType;

import static io.snice.preconditions.PreConditions.assertArgument;
import static io.snice.preconditions.PreConditions.assertNotNull;

/**
 *
 */
public interface MccMncType extends GtpType {

    static MccMncType parse(final Buffer buffer) {
        assertArgument(buffer != null && buffer.capacity() > 0, "The buffer cannot be null or empty");
        return new DefaultMccMncType(buffer, MccMnc.parseAsMccMnc(buffer));
    }

    static MccMncType parse(final String buffer) {
        assertNotNull(buffer, "The buffer cannot be null");
        final var mccMnc = MccMnc.of(buffer);
        return new DefaultMccMncType(mccMnc.toBuffer(), mccMnc);
    }

    static MccMncType ofValue(final Buffer buffer) {
        return parse(buffer);
    }

    static MccMncType ofValue(final String buffer) {
        return parse(buffer);
    }

    MccMnc getMccMnc();

    class DefaultMccMncType extends ImmutableGtpType<MccMncType> implements MccMncType {
        private final MccMnc mccMnc;

        private DefaultMccMncType(final Buffer buffer, final MccMnc mccMnc) {
            super(buffer);
            this.mccMnc = mccMnc;
        }

        @Override
        public String toString() {
            return mccMnc.toString();
        }

        @Override
        public MccMnc getMccMnc() {
            return mccMnc;
        }
    }
}
