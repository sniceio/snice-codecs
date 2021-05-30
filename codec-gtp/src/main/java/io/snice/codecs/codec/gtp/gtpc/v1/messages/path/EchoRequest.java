package io.snice.codecs.codec.gtp.gtpc.v1.messages.path;

import io.snice.buffer.Buffer;
import io.snice.codecs.codec.gtp.gtpc.InfoElement;
import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1Header;
import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1MessageType;
import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1Request;
import io.snice.codecs.codec.gtp.gtpc.v1.impl.AbstractGtp1MessageFramer;
import io.snice.codecs.codec.gtp.gtpc.v1.impl.GenericGtp1MessageBuilder;
import io.snice.codecs.codec.gtp.gtpc.v1.impl.Gtp1MessageBuilder;
import io.snice.codecs.codec.gtp.gtpc.v1.impl.Gtp1MessageFramer;
import io.snice.codecs.codec.gtp.gtpc.v1.impl.ImmutableGtp1Request;

import java.util.List;

import static io.snice.codecs.codec.gtp.gtpc.v1.Gtp1MessageType.ECHO_REQUEST;
import static io.snice.preconditions.PreConditions.assertArgument;

public interface EchoRequest extends Gtp1Request {

    static Gtp1MessageFramer<EchoRequest> from(final Gtp1MessageType type, final Gtp1Header header, final Buffer buffer) {
        assertArgument(type == ECHO_REQUEST);
        return new EchoRequestFramer(type, header, buffer);
    }

    static Gtp1MessageBuilder<EchoRequest> create() {
        return new EchoRequestBuilder();
    }

    static Gtp1MessageBuilder<EchoRequest> create(final Gtp1Header header) {
        assertArgument(header.getType() == ECHO_REQUEST);
        return new EchoRequestBuilder(header);
    }

    @Override
    default EchoRequest toEchoRequest() {
        return this;
    }

    Gtp1MessageBuilder<EchoResponse> createResponse();

    class EchoRequestFramer extends AbstractGtp1MessageFramer<EchoRequest> {

        private EchoRequestFramer(final Gtp1MessageType type, final Gtp1Header header, final Buffer buffer) {
            super(type, header, buffer);
        }

        @Override
        protected EchoRequest internalBuild(final Gtp1MessageType type, final Buffer buffer, final List<InfoElement> ies, final Gtp1Header header, final Buffer payload) {
            return new EchoRequestImpl(type, header, ies, buffer);
        }
    }

    class EchoRequestBuilder extends GenericGtp1MessageBuilder<EchoRequest> {

        private EchoRequestBuilder() {
            super(Gtp1MessageType.ECHO_REQUEST);
        }

        private EchoRequestBuilder(final Gtp1Header header) {
            super(Gtp1MessageType.ECHO_REQUEST, header);
        }

        @Override
        protected EchoRequest internalBuild(final Gtp1MessageType type, final Buffer buffer, final Gtp1Header header,
                                            final List<InfoElement> ies, final Buffer payload) {
            return new EchoRequestImpl(type, header, ies, buffer);
        }
    }

    class EchoRequestImpl extends ImmutableGtp1Request implements EchoRequest {

        private EchoRequestImpl(final Gtp1MessageType type,
                                final Gtp1Header header,
                                final List<InfoElement> ies,
                                final Buffer buffer) {
            super(type, header, buffer, ies, null);
        }

        @Override
        public Gtp1Request toGtp1Request() {
            return this;
        }

        @Override
        public Gtp1MessageBuilder<EchoResponse> createResponse() {
            final var responseHeader = getHeader().toGtp1Header().copy()
                    .withType(Gtp1MessageType.ECHO_RESPONSE)
                    .build();
            return EchoResponse.create(responseHeader);
        }

    }
}
