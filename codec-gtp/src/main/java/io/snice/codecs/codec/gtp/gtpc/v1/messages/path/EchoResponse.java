package io.snice.codecs.codec.gtp.gtpc.v1.messages.path;

import io.snice.buffer.Buffer;
import io.snice.codecs.codec.gtp.gtpc.InfoElement;
import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1Header;
import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1MessageType;
import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1Response;
import io.snice.codecs.codec.gtp.gtpc.v1.impl.AbstractGtp1MessageFramer;
import io.snice.codecs.codec.gtp.gtpc.v1.impl.GenericGtp1MessageBuilder;
import io.snice.codecs.codec.gtp.gtpc.v1.impl.Gtp1MessageBuilder;
import io.snice.codecs.codec.gtp.gtpc.v1.impl.Gtp1MessageFramer;
import io.snice.codecs.codec.gtp.gtpc.v1.impl.ImmutableGtp1Response;

import java.util.List;

import static io.snice.codecs.codec.gtp.gtpc.v1.Gtp1MessageType.ECHO_RESPONSE;
import static io.snice.preconditions.PreConditions.assertArgument;

public interface EchoResponse extends Gtp1Response {

    static Gtp1MessageFramer<EchoResponse> from(final Gtp1MessageType type, final Gtp1Header header, final Buffer buffer) {
        assertArgument(type == ECHO_RESPONSE);
        return new EchoResponseFramer(type, header, buffer);
    }

    static Gtp1MessageBuilder<EchoResponse> create() {
        return new EchoResponseBuilder();
    }

    static Gtp1MessageBuilder<EchoResponse> create(final Gtp1Header header) {
        assertArgument(header.getType() == ECHO_RESPONSE);
        return new EchoResponseBuilder(header);
    }

    default EchoResponse toEchoResponse() {
        return this;
    }

    class EchoResponseBuilder extends GenericGtp1MessageBuilder<EchoResponse> {

        private EchoResponseBuilder() {
            super(ECHO_RESPONSE);
        }

        private EchoResponseBuilder(final Gtp1Header header) {
            super(ECHO_RESPONSE, header);
        }

        @Override
        protected EchoResponse internalBuild(final Gtp1MessageType type, final Buffer buffer, final Gtp1Header header,
                                             final List<InfoElement> ies, final Buffer payload) {
            return new EchoResponseImpl(type, header, ies, buffer);
        }
    }

    class EchoResponseFramer extends AbstractGtp1MessageFramer<EchoResponse> {

        private EchoResponseFramer(final Gtp1MessageType type, final Gtp1Header header, final Buffer buffer) {
            super(type, header, buffer);
        }

        @Override
        protected EchoResponse internalBuild(final Gtp1MessageType type, final Buffer buffer, final List<InfoElement> ies, final Gtp1Header header, final Buffer payload) {
            return new EchoResponseImpl(type, header, ies, buffer);
        }
    }

    class EchoResponseImpl extends ImmutableGtp1Response implements EchoResponse {

        private EchoResponseImpl(final Gtp1MessageType type,
                                 final Gtp1Header header,
                                 final List<InfoElement> ies,
                                 final Buffer body) {
            super(type, header, body, ies, null);
        }

        @Override
        public Gtp1Response toGtp1Response() {
            return this;
        }
    }
}
