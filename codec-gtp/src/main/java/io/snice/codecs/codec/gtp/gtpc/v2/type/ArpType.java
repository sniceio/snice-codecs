package io.snice.codecs.codec.gtp.gtpc.v2.type;


import io.snice.buffer.Buffer;
import io.snice.buffer.Buffers;
import io.snice.buffer.WritableBuffer;
import io.snice.codecs.codec.gtp.gtpc.v2.type.impl.ImmutableGtpType;

import static io.snice.preconditions.PreConditions.assertArgument;

/**
 * The Allocation-Retention-Priority (TS 29.212 section 5.3.32) is an AVP but
 * is also encoded in a single byte within the GTPv2 IE Bearer Quality of Service (TS 29.274 section 8.15).
 * Since the Bearer QoS essentially has one ARP and then a more generic QoS type (note, it's not a grouped IE)
 * they were split in two since the Flow QoS only make use of the {@link QosType} whereas the Bearer QoS would use
 * {@link ArpType} and {@link QosType}.
 */
public interface ArpType extends GtpType {

    static ArpType parse(final Buffer buffer) {
        assertArgument(buffer != null && buffer.capacity() == 1, "The buffer must be exactly 1 byte long");
        final boolean pvi = !buffer.getBit0(0); // 0 is enabled, 1 is disabled.
        final boolean pci = !buffer.getBit6(0); // 0 is enabled, 1 is disabled.
        final int pl = (buffer.getByte(0) >> 2) & 0x0F; // Priority level are the 4 bits in the middle of the byte
        return new DefaultArpType(buffer, pl, pci, pvi);
    }

    static ArpType parse(final String buffer) {
        assertArgument(buffer != null && buffer.length() == 1, "The buffer must be exactly 1 byte long");
        return parse(Buffers.wrap(buffer));
    }

    static ArpType ofValue(final Buffer buffer) {
        return parse(buffer);
    }

    static ArpType ofValue(final String buffer) {
        return parse(buffer);
    }

    static ArpType ofValue(final int priorityLevel, final boolean pci, final boolean pvi) {
        assertArgument(priorityLevel >= 0 && priorityLevel < 16, "The Priority Level can only be between 0-15");
        final var writable = WritableBuffer.of(1).fastForwardWriterIndex();
        writable.setByte(0, (byte) ((priorityLevel & 0x0F) << 2));
        writable.setBit0(0, !pvi);
        writable.setBit6(0, !pci);
        return new DefaultArpType(writable.build(), priorityLevel, pci, pvi);
    }

    boolean isPreEmptionVulnerability();

    boolean isPreEmptionCapability();

    int getPriorityLevel();

    class DefaultArpType extends ImmutableGtpType<ArpType> implements ArpType {

        /**
         * Pre-emption Vulnerability (TS 29.212 section 5.3.47)
         */
        private final boolean pvi;

        /**
         * Pre-emption Capability (TS 29.212 section 5.3.46)
         */
        private final boolean pci;

        /**
         * Priority Level (TS 29.212 section 5.3.45)
         */
        private final int pl;

        private DefaultArpType(final Buffer buffer, final int pl,
                               final boolean pci, final boolean pvi) {
            super(buffer);
            this.pl = pl;
            this.pci = pci;
            this.pvi = pvi;
        }

        @Override
        public boolean isPreEmptionVulnerability() {
            return pvi;
        }

        @Override
        public boolean isPreEmptionCapability() {
            return pci;
        }

        @Override
        public int getPriorityLevel() {
            return pl;
        }
    }
}
