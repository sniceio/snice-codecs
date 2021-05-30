package io.snice.codecs.codec.gtp.gtpc.v2.type;

import io.snice.buffer.Buffer;
import io.snice.buffer.Buffers;
import io.snice.codecs.codec.gtp.gtpc.v2.type.impl.ImmutableGtpType;

import static io.snice.preconditions.PreConditions.assertArgument;
import static io.snice.preconditions.PreConditions.assertNotNull;

public interface BearerQosType extends GtpType {

    static BearerQosType parse(final Buffer buffer) {
        assertArgument(buffer != null && buffer.capacity() == 22, "The buffer must be exactly 18 bytes long");
        final var arp = buffer.slice(0, 1);
        final var qos = buffer.slice(1, buffer.capacity());

        return new DefaultBearerQosType(buffer, ArpType.parse(arp), QosType.parse(qos));
    }

    static BearerQosType parse(final String buffer) {
        assertArgument(buffer != null && buffer.length() == 22, "The buffer must be exactly 18 bytes long");
        return parse(Buffers.wrap(buffer));
    }

    static BearerQosType ofValue(final Buffer buffer) {
        return parse(buffer);
    }

    static BearerQosType ofValue(final String buffer) {
        return parse(buffer);
    }

    static BearerQosType ofValue(final ArpType arp, final QosType qos) {
        assertNotNull(arp);
        assertNotNull(qos);
        return new DefaultBearerQosType(Buffers.wrap(arp.getBuffer(), qos.getBuffer()), arp, qos);
    }

    ArpType getArp();

    QosType getQos();

    class DefaultBearerQosType extends ImmutableGtpType<BearerQosType> implements BearerQosType {

        private final ArpType arp;
        private final QosType qos;

        private DefaultBearerQosType(final Buffer buffer, final ArpType arp, final QosType qos) {
            super(buffer);
            this.arp = arp;
            this.qos = qos;
        }

        @Override
        public ArpType getArp() {
            return arp;
        }

        @Override
        public QosType getQos() {
            return qos;
        }
    }
}
