package io.snice.codecs.codec.gtp.gtpc.v2.messages.tunnel;

import io.snice.buffer.Buffer;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Header;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2MessageType;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Request;
import io.snice.codecs.codec.gtp.gtpc.v2.Impl.*;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.TypeLengthInstanceValue;

import java.util.List;

import static io.snice.preconditions.PreConditions.assertArgument;

public interface DeleteSessionRequest extends Gtp2Request {

    static Gtp2MessageFramer<DeleteSessionRequest> from(final Gtp2MessageType type, final Gtp2Header header, final Buffer buffer) {
        assertArgument(type == Gtp2MessageType.DELETE_SESSION_REQUEST);
        return new DeleteSessionRequestFramer(type, header, buffer);
    }

    static Gtp2MessageBuilder<DeleteSessionRequest> create() {
        return new DeleteSessionRequestBuilder();
    }

    static Gtp2MessageBuilder<DeleteSessionRequest> create(final Gtp2Header header) {
        assertArgument(header.getType() == Gtp2MessageType.DELETE_SESSION_REQUEST);
        return new DeleteSessionRequestBuilder(header);
    }


    @Override
    default DeleteSessionRequest toDeleteSessionRequest() {
        return this;
    }

    @Override
    Gtp2MessageBuilder<CreateSessionResponse> createResponse();

    class DeleteSessionRequestFramer extends AbstractGtp2MessageFramer<DeleteSessionRequest> {

        protected DeleteSessionRequestFramer(final Gtp2MessageType type, final Gtp2Header header, final Buffer buffer) {
            super(type, header, buffer);
        }

        @Override
        protected DeleteSessionRequest internalBuild(final Gtp2MessageType type, final Buffer buffer, final Gtp2Header header, final List<TypeLengthInstanceValue> tlivs) {
            return new DeleteSessionRequestImpl(type, header, buffer, tlivs);
        }
    }

    class DeleteSessionRequestBuilder extends AbstractGtp2MessageBuilder<DeleteSessionRequest> {

        protected DeleteSessionRequestBuilder() {
            super(Gtp2MessageType.DELETE_SESSION_REQUEST);
        }

        private DeleteSessionRequestBuilder(final Gtp2Header header) {
            super(header);
        }

        @Override
        protected TypeLengthInstanceValue process(final int index, final TypeLengthInstanceValue tliv) {
            return tliv;
        }

        @Override
        protected DeleteSessionRequest internalBuild(final Gtp2MessageType type, final Buffer buffer, final Gtp2Header header, final List<TypeLengthInstanceValue> tlivs) {
            return new DeleteSessionRequestImpl(type, header, buffer, tlivs);
        }
    }

    class DeleteSessionRequestImpl extends ImmutableGtp2Request implements DeleteSessionRequest {

        private DeleteSessionRequestImpl(final Gtp2MessageType type,
                                         final Gtp2Header header,
                                         final Buffer body,
                                         final List<TypeLengthInstanceValue> values) {
            super(type, header, body, values);
        }

        @Override
        public Gtp2MessageBuilder<CreateSessionResponse> createResponse() {
            final var responseHeader = getHeader().toGtp2Header().copy()
                    .withType(Gtp2MessageType.DELETE_SESSION_RESPONSE)
                    .build();
            return DeleteSessionResponse.create(responseHeader);
        }
    }
}
