package io.snice.codecs.codec.gtp.gtpc.v2.messages.path;

import io.snice.buffer.Buffer;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Header;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2MessageType;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Request;
import io.snice.codecs.codec.gtp.gtpc.v2.Impl.*;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.Recovery;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.TypeLengthInstanceValue;

import java.util.List;

import static io.snice.codecs.codec.gtp.gtpc.v2.Gtp2MessageType.ECHO_REQUEST;
import static io.snice.preconditions.PreConditions.assertArgument;

public interface EchoRequest extends Gtp2Request {

    static Gtp2MessageFramer<EchoRequest> from(final Gtp2MessageType type, final Gtp2Header header, final Buffer buffer) {
        assertArgument(type == ECHO_REQUEST, "Wrong type. Type given " + type + " but expected is " + ECHO_REQUEST);
        return new EchoRequestFramer(type, header, buffer);
    }

    static Gtp2MessageBuilder<EchoRequest> create() {
        return new EchoRequestBuilder();
    }

    Recovery getRecovery();

    @Override
    default Gtp2MessageType getType() {
        return ECHO_REQUEST;
    }

    @Override
    Gtp2MessageBuilder<EchoResponse> createResponse();

    @Override
    default boolean isEchoRequest() {
        return true;
    }

    class EchoRequestFramer extends AbstractGtp2MessageFramer<EchoRequest> {

        /**
         * Recovery TLIV is mandatory
         */
        private Recovery recovery;

        private EchoRequestFramer(final Gtp2MessageType type, final Gtp2Header header, final Buffer buffer) {
            super(type, header, buffer);
        }

        @Override
        protected TypeLengthInstanceValue process(final int index, final TypeLengthInstanceValue tliv) {
            switch (tliv.getType()) {
                case Recovery.TYPE_VALUE:
                    recovery = (Recovery) tliv.ensure();
                    return recovery;
                default:
                    return tliv;
            }
        }

        @Override
        protected EchoRequest internalBuild(final Gtp2MessageType type, final Buffer buffer, final Gtp2Header header, final List<TypeLengthInstanceValue> tlivs) {
            return new EchoRequestImpl(type, header, buffer, tlivs, recovery);
        }
    }

    class EchoRequestBuilder extends AbstractGtp2MessageBuilder<EchoRequest> {

        private Recovery recovery;

        private EchoRequestBuilder() {
            super(ECHO_REQUEST);
        }

        private EchoRequestBuilder(final Gtp2Header header) {
            super(header);
        }

        @Override
        protected TypeLengthInstanceValue process(final int index, final TypeLengthInstanceValue tliv) {
            switch (tliv.getType()) {
                case Recovery.TYPE_VALUE:
                    recovery = (Recovery) tliv.ensure();
                    return recovery;
                default:
                    return tliv;
            }
        }

        @Override
        protected EchoRequest internalBuild(final Gtp2MessageType type, final Buffer buffer, final Gtp2Header header, final List<TypeLengthInstanceValue> tlivs) {
            return new EchoRequestImpl(type, header, buffer, tlivs, recovery);
        }
    }

    class EchoRequestImpl extends ImmutableGtp2Request implements EchoRequest {

        private final Recovery recovery;

        private EchoRequestImpl(final Gtp2MessageType type,
                                final Gtp2Header header,
                                final Buffer body,
                                final List<TypeLengthInstanceValue> values,
                                final Recovery recovery) {
            super(type, header, body, values);
            this.recovery = recovery;
        }

        @Override
        public Gtp2Request toGtp2Request() {
            return this;
        }

        @Override
        public Recovery getRecovery() {
            return recovery;
        }

        @Override
        public Gtp2MessageBuilder<EchoResponse> createResponse() {
            final var responseHeader = getHeader().toGtp2Header().copy()
                    .withType(Gtp2MessageType.ECHO_RESPONSE)
                    .build();
            return EchoResponse.create(responseHeader);
        }
    }

}
