package io.snice.codecs.codec.gtp.type;

import io.snice.buffer.Buffer;
import io.snice.buffer.Buffers;
import io.snice.buffer.WritableBuffer;
import io.snice.codecs.codec.gtp.Teid;
import io.snice.codecs.codec.gtp.type.impl.ImmutableGtpType;
import io.snice.codecs.codec.tgpp.ReferencePoint;

import java.util.Optional;

import static io.snice.preconditions.PreConditions.assertArgument;
import static io.snice.preconditions.PreConditions.assertNotNull;

public interface FTeidType extends GtpType {

    static FTeidType parse(final Buffer buffer) {
        assertArgument(buffer != null && buffer.capacity() >= 3, "The buffer must be at least 3 bytes long");
        final Buffer ipv4Address;
        final Buffer ipv6Address = null; // we don't support ipv6 just yet

        final var teid = Teid.of(buffer.slice(1, 1 + 4));

        // index of next ip address
        int index = 1 + 4;
        if (buffer.getBit7(0)) {
            ipv4Address = buffer.slice(index, index + 4);
            index += 4; // jump to the ipv6 if present...
        } else {
            ipv4Address = null;
        }


        if (buffer.getBit6(0)) {
            // here we would use the above index to read out 16 bytes for the IPv6 address
            // except we don't support it just yet...
            throw new IllegalArgumentException("Sorry, don't do IPv6 address just yet");
        }

        return new DefaultFTeidType(buffer, teid, ipv4Address, ipv6Address);
    }

    static FTeidType parse(final String buffer) {
        return parse(Buffers.wrap(buffer));
    }

    static FTeidType ofValue(final Buffer buffer) {
        return parse(buffer);
    }

    static FTeidType ofValue(final String buffer) {
        return parse(buffer);
    }

    Teid getTeid();

    Optional<Buffer> getIpv4Address();

    Optional<String> getIpv4AddressAsString();

    Optional<Buffer> getIpv6Address();

    static Builder create() {
        return new Builder();
    }

    class Builder {

        private Buffer ipv4Address;
        private Buffer ipv6Address;

        private int interfaceType = -1;
        private Teid teid;

        private Builder() {
            // left empty intentionally
        }

        public Builder withTeid(final Teid teid) {
            this.teid = teid;
            return this;
        }

        /**
         * Ask the builder to generate a random {@link Teid}
         */
        public Builder withRandomizedTeid() {
            this.teid = Teid.random();
            return this;
        }

        /**
         * Not sure about this one. need to do some research...
         *
         * @param interfaceType
         * @param gtpc
         * @return
         */
        public Builder withReferencePoint(final ReferencePoint interfaceType, final boolean gtpc) {
            assertNotNull(interfaceType);
            switch (interfaceType) {
                case S5:
                    // S5 / S8 PGW GTP -C interface
                    // but, it's a bit odd since you also need to know if this is between
                    // SGW <-> PGW or, I guess, MME <-> SGW. TODO: need to read the specs
                    if (gtpc) {
                        this.interfaceType = 6;
                    } else {
                        this.interfaceType = 4;
                    }
                    return this;
                default:
                    throw new IllegalArgumentException("Unknown Reference Point");
            }
        }

        public Builder withIPv4Address(final String address) {
            this.ipv4Address = Buffers.wrapAsIPv4(address);
            return this;
        }

        public Builder withIPv6Address(final String address) {
            throw new RuntimeException("Sorry, haven't implemented IPv6 just yet");
        }

        public FTeidType build() {
            assertArgument(ipv4Address != null || ipv6Address != null, "You must specify at least one IPv4 or IPv6 address or both");
            assertArgument(teid != null, "You must specify the TEID");
            assertArgument(interfaceType >= 0, "You must specify the interface type");
            // size: 1 byte = v4, v6 and the interface type
            // size: 4 byte TEID/GRE key
            // size: 4 byte IPv4 address if present
            // size: 16 byte IPv6 address if present
            // one of the IPv4 or IPv6 MUST be present, or both.
            final int size = 1 + 4 + (ipv4Address != null ? 4 : 0) + (ipv6Address != null ? 16 : 0);
            final var writable = WritableBuffer.of(size).fastForwardWriterIndex();

            // TODO: interface type
            final int value = interfaceType & 0b00111111;
            writable.setByte(0, (byte) value);

            if (ipv4Address != null) {
                writable.setBit(0, 7, true);
                writable.setInt(5, ipv4Address.getInt(0));
            }

            if (ipv6Address != null) {
                throw new RuntimeException("Sorry, but haven't implemented IPv6 address support just yet");
            }

            final var teidBuffer = teid.getBuffer();
            writable.setByte(1, teidBuffer.getByte(0));
            writable.setByte(2, teidBuffer.getByte(1));
            writable.setByte(3, teidBuffer.getByte(2));
            writable.setByte(4, teidBuffer.getByte(3));

            return new DefaultFTeidType(writable.build(), teid, ipv4Address, ipv6Address);
        }

    }

    // TODO need e.g.
    // Optional<IPv4Address> getIPv4Address();
    // Optional<IPv6Address> getIPv6Address();

    class DefaultFTeidType extends ImmutableGtpType<FTeidType> implements FTeidType {
        private final Teid teid;
        private final Optional<Buffer> ipv4Address;
        private final Optional<Buffer> ipv6Address;

        private DefaultFTeidType(final Buffer buffer, final Teid teid, final Buffer ipv4Address, final Buffer ipv6Address) {
            super(buffer);
            this.teid = teid;
            this.ipv4Address = Optional.ofNullable(ipv4Address);
            this.ipv6Address = Optional.ofNullable(ipv6Address);
        }

        @Override
        public Teid getTeid() {
            return teid;
        }

        @Override
        public Optional<Buffer> getIpv4Address() {
            return ipv4Address;
        }

        @Override
        public Optional<String> getIpv4AddressAsString() {
            return ipv4Address.map(b -> b.toIPv4String(0));
        }

        @Override
        public Optional<Buffer> getIpv6Address() {
            return ipv6Address;
        }
    }
}
