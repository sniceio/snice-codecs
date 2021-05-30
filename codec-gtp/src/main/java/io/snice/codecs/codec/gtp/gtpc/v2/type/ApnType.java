package io.snice.codecs.codec.gtp.gtpc.v2.type;

import io.snice.buffer.Buffer;
import io.snice.buffer.Buffers;
import io.snice.buffer.WritableBuffer;
import io.snice.codecs.codec.gtp.GtpParseException;
import io.snice.codecs.codec.gtp.gtpc.v2.type.impl.ImmutableGtpType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static io.snice.preconditions.PreConditions.assertArgument;
import static io.snice.preconditions.PreConditions.assertNotEmpty;

/**
 * The encoding of the APN is described in TS 23.003 section 9 and the main thing is really
 * that each label has an octet in front of it describing the lengh of the coming label. In
 * documentation, you'll see "dots" between labels instead.
 * <p>
 * So, "hello.world" would be encoded as
 * byte0: 5 (for the length of hello)
 * byte1-6: "hello"
 * byte7: 4 (for the length of "world"
 * byte8-12: "world"
 * <p>
 * Also, each label is only allowed A-Z, a-z, 0-9 and '-'.
 */
public interface ApnType extends GtpType {

    static ApnType parse(final Buffer buffer) {
        assertArgument(buffer != null && buffer.capacity() > 1, "The APN must be at least 2 character long");
        final var labels = nextLabel(buffer, new ArrayList());
        return new DefaultApnType(buffer, Collections.unmodifiableList(labels));
    }

    static ApnType parse(final String buffer) {
        assertNotEmpty(buffer);
        return parse(Buffers.wrap(buffer));
    }

    static ApnType ofValue(final Buffer buffer) {
        assertArgument(buffer != null && buffer.capacity() > 0, "The APN must be at least 1 character long");
        return ofValue(buffer.toString());
    }

    /**
     * Specify the value in the form of "human readable APN", meaning, include the "dots"
     * such as "hello.world" or simply just "hello" if there is a single label.
     *
     * @param apn
     * @return
     */
    static ApnType ofValue(final String apn) {
        assertNotEmpty(apn, "The APN cannot be null or the empty String");
        assertArgument(apn.length() > 0, "The APN must be a least 1 character long");

        final var buffer = WritableBuffer.of(apn.length() + 1).fastForwardWriterIndex();
        parse(apn, 0, 0, buffer);
        return ApnType.parse(buffer.build());
    }

    private static void parse(final String apn, final int size, final int index, final WritableBuffer buffer) {
        if (apn.length() == index) {
            buffer.setByte(index - size, (byte) size);
            return;
        }

        final char ch = apn.charAt(index);
        if (ch == '.') {
            buffer.setByte(index - size, (byte) size);
            parse(apn, 0, index + 1, buffer);
            return;
        } else {
            buffer.setByte(index + 1, (byte) ch);
            parse(apn, size + 1, index + 1, buffer);
            return;
        }

    }

    private static List<Buffer> nextLabel(final Buffer buffer, final List<Buffer> current) {
        if (buffer.isEmpty()) {
            return current;
        }

        final short length = buffer.getUnsignedByte(0);
        try {
            final var label = buffer.slice(1, 1 + length);
            current.add(label);
            return nextLabel(buffer.slice(1 + length, buffer.capacity()), current);
        } catch (final IndexOutOfBoundsException e) {
            final var msg = "Not enough to read next APN label. Expected length: " + length
                    + " but only " + (buffer.capacity() - 1) + " was available";
            throw new GtpParseException(1, msg);
        }
    }

    List<Buffer> getLabels();

    class DefaultApnType extends ImmutableGtpType<ApnType> implements ApnType {

        private final List<Buffer> labels;

        protected DefaultApnType(final Buffer buffer, final List<Buffer> labels) {
            super(buffer);
            this.labels = labels;
        }

        @Override
        public List<Buffer> getLabels() {
            return labels;
        }

        @Override
        public String toString() {
            return labels.stream().map(Buffer::toString).collect(Collectors.joining("."));
        }
    }
}
