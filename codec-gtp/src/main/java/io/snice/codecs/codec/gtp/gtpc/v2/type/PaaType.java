package io.snice.codecs.codec.gtp.gtpc.v2.type;

import io.snice.buffer.Buffer;
import io.snice.buffer.Buffers;
import io.snice.codecs.codec.gtp.GtpParseException;
import io.snice.codecs.codec.gtp.gtpc.v2.type.impl.ImmutableGtpType;
import io.snice.net.IPv4;

import java.util.Optional;

/**
 *
 */
public interface PaaType extends GtpType {

    static PaaType parse(final Buffer buffer) {
        // 1 for header, 4 for IPv4 ,which would be the smallest buffer we can have.
        Buffers.assertBufferCapacityAtLeast(buffer, 1 + 4, "The PAA must be at least 5 bytes long");
        final var pdn = PdnType.ofValue(buffer.getByte(0));
        if (pdn.getType() != PdnType.Type.IPv4) {
            throw new GtpParseException(1, "No support for IPv6 addresses at the moment");
        }

        final var ipv4Address = buffer.slice(1, 5);
        return new DefaultPaaType(buffer, pdn, ipv4Address);
    }

    static PaaType parse(final String buffer) {
        throw new IllegalArgumentException("Currently don't support parsing from String");
    }

    static PaaType ofValue(final Buffer buffer) {
        return parse(buffer);
    }

    static PaaType ofValue(final String buffer) {
        return parse(buffer);
    }

    static PaaType fromIPv4(final String ipv4Address) {
        final var ipv4Buffer = Buffers.wrap(IPv4.fromString(ipv4Address));
        final var pdnTypeBuffer = PdnType.Type.IPv4.getBuffer();
        final var buffer = Buffers.wrap(pdnTypeBuffer, ipv4Buffer);
        return new DefaultPaaType(buffer, PdnType.of(PdnType.Type.IPv4), ipv4Buffer);
    }

    PdnType getPdnType();

    Optional<Buffer> getIPv4Address();

    class DefaultPaaType extends ImmutableGtpType<PaaType> implements PaaType {
        private final PdnType pdn;
        private final Optional<Buffer> ipv4Address;

        private DefaultPaaType(final Buffer buffer, final PdnType pdn, final Buffer ipv4Address) {
            super(buffer);
            this.pdn = pdn;
            this.ipv4Address = Optional.ofNullable(ipv4Address);
        }

        @Override
        public String toString() {
            // TODO: should dump the entire representation
            // Also, note that right now, ipv4address must be set because we won't
            // parse anything else. Hence, the 'get' is safe to do
            return ipv4Address.get().toIPv4String(0);
        }

        @Override
        public PdnType getPdnType() {
            return pdn;
        }

        @Override
        public Optional<Buffer> getIPv4Address() {
            return ipv4Address;
        }
    }
}
