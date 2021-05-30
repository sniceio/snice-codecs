package io.snice.codecs.codec.internet.ipv4;

import io.snice.buffer.Buffer;
import io.snice.buffer.WritableBuffer;
import io.snice.net.IPv4;

import static io.snice.buffer.Buffers.assertBufferCapacity;

public interface IPv4Builder<T> {

    int IPV4_HEADER_LENGTH = 20;

    IPv4Builder<T> withSourceIp(String sourceIp);

    IPv4Builder<T> withSourceIp(Buffer sourceIp);

    IPv4Builder<T> withDestinationIp(String destinationIp);

    IPv4Builder<T> withDestinationIp(Buffer destinationIp);

    IPv4Builder<T> withTTL(int ttl);

    IPv4Message<T> build();

    abstract class BaseBuilder<T> implements IPv4Builder<T> {

        protected WritableBuffer buffer;

        protected BaseBuilder(final WritableBuffer buffer) {
            this.buffer = buffer;
            buffer.setBit6(0, true); // version 4
        }

        @Override
        public IPv4Builder<T> withSourceIp(final String sourceIp) {
            return setIp(12, IPv4.fromString(sourceIp));
        }

        @Override
        public IPv4Builder<T> withSourceIp(final Buffer sourceIp) {
            return setIp(12, sourceIp);
        }

        @Override
        public IPv4Builder<T> withDestinationIp(final String destinationIp) {
            return setIp(16, IPv4.fromString(destinationIp));
        }

        @Override
        public IPv4Builder<T> withDestinationIp(final Buffer destinationIp) {
            return setIp(16, destinationIp);
        }

        public IPv4Builder<T> withTTL(final int ttl) {
            buffer.setByte(8, (byte)ttl);
            return this;
        }

        private IPv4Builder<T> setIp(final int index, final byte[] ip) {
            buffer.setByte(index + 0, ip[0]);
            buffer.setByte(index + 1, ip[1]);
            buffer.setByte(index + 2, ip[2]);
            buffer.setByte(index + 3, ip[3]);
            return this;
        }

        private IPv4Builder<T> setIp(final int index, final Buffer ip) {
            assertBufferCapacity(ip, 4, "The IP buffer must be exactly 4 bytes");
            buffer.setByte(index + 0, ip.getByte(0));
            buffer.setByte(index + 1, ip.getByte(1));
            buffer.setByte(index + 2, ip.getByte(2));
            buffer.setByte(index + 3, ip.getByte(3));
            return this;
        }
    }
}
