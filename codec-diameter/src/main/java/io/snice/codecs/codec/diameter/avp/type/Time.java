package io.snice.codecs.codec.diameter.avp.type;

import io.snice.buffer.Buffer;
import io.snice.buffer.WritableBuffer;
import io.snice.preconditions.PreConditions;

import static io.snice.preconditions.PreConditions.assertArgument;
import static io.snice.preconditions.PreConditions.assertNotNull;

public interface Time extends DiameterType {

    static Time parse(final Buffer data) {
        assertNotNull(data, "The data for the Time Diameter Type cannot be null");
        assertArgument(data.capacity() == 4, "The Time Diameter Type MUST be exactly 4 bytes");
        return new Time.DefaultTime(data);
    }

    class DefaultTime implements Time {
        private final Buffer value;

        private DefaultTime(final Buffer value) {
            this.value = value;
        }

        @Override
        public void writeValue(final WritableBuffer buffer) {
            value.writeTo(buffer);
        }

        @Override
        public int size() {
            return 4;
        }

    }
}
