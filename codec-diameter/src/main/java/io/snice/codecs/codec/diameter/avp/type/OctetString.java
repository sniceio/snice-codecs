package io.snice.codecs.codec.diameter.avp.type;

import io.snice.buffer.Buffer;
import io.snice.buffer.Buffers;
import io.snice.buffer.WritableBuffer;

import java.util.Objects;

public interface OctetString extends DiameterType {


    static OctetString parse(final Buffer data) {
        return new DefaultOctetString(data, false);
    }

    static OctetString parse(final String data) {
        return new DefaultOctetString(Buffers.wrap(data), false);
    }

    static OctetString parse(final Buffer data, final boolean isEncodedAsTBCD) {
        return new DefaultOctetString(data, isEncodedAsTBCD);
    }

    static OctetString parse(final int data) {
        // TODO: not sure this makes sense.
        return parse(String.valueOf(data));
    }

    static OctetString parse(final long data) {
        // TODO: not sure this makes sense.
        return parse(String.valueOf(data));
    }

    String getValue();

    class DefaultOctetString implements OctetString {
        private final boolean isEncodedAsTBCD;
        private final Buffer value;

        private DefaultOctetString(final Buffer value, final boolean isEncodedAsTBCD) {
            this.value = value;
            this.isEncodedAsTBCD = isEncodedAsTBCD;
        }

        @Override
        public void writeValue(final WritableBuffer buffer) {
            value.writeTo(buffer);
        }

        @Override
        public String getValue() {
            if (isEncodedAsTBCD) {
                return value.toTBCD();
            }
            return value.toString();
        }

        @Override
        public String toString() {
            return getValue();
        }

        @Override
        public int size() {
            return value.capacity();
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final DefaultOctetString that = (DefaultOctetString) o;
            return Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }
}
