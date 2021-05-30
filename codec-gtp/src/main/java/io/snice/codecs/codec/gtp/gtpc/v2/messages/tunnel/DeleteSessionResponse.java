package io.snice.codecs.codec.gtp.gtpc.v2.messages.tunnel;

import io.snice.buffer.Buffer;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Header;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2MessageType;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Response;
import io.snice.codecs.codec.gtp.gtpc.v2.Impl.*;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.TypeLengthInstanceValue;

import java.util.List;

import static io.snice.codecs.codec.gtp.gtpc.v2.Gtp2MessageType.DELETE_SESSION_RESPONSE;
import static io.snice.preconditions.PreConditions.assertArgument;

public interface DeleteSessionResponse extends Gtp2Response {

    static Gtp2MessageFramer<DeleteSessionResponse> from(final Gtp2MessageType type, final Gtp2Header header, final Buffer buffer) {
        assertArgument(type == DELETE_SESSION_RESPONSE);
        return new DeleteSessionResponseFramer(type, header, buffer);
    }

    static Gtp2MessageBuilder<CreateSessionResponse> create(final Gtp2Header header) {
        assertArgument(header.getType() == DELETE_SESSION_RESPONSE);
        return new DeleteSessionResponseBuilder(header);
    }

    static Gtp2MessageBuilder<CreateSessionResponse> create() {
        return new DeleteSessionResponseBuilder();
    }

    class DeleteSessionResponseFramer extends AbstractGtp2MessageFramer<DeleteSessionResponse> {

        private DeleteSessionResponseFramer(final Gtp2MessageType type, final Gtp2Header header, final Buffer buffer) {
            super(type, header, buffer);
        }

        @Override
        protected DeleteSessionResponse internalBuild(final Gtp2MessageType type, final Buffer buffer, final Gtp2Header header, final List<TypeLengthInstanceValue> tlivs) {
            return new DeleteSessionResponseImpl(type, header, buffer, tlivs);
        }

    }

    class DeleteSessionResponseBuilder extends AbstractGtp2MessageBuilder<CreateSessionResponse> {
        protected DeleteSessionResponseBuilder() {
            super(DELETE_SESSION_RESPONSE);
        }

        protected DeleteSessionResponseBuilder(final Gtp2Header header) {
            super(header);
        }

        @Override
        protected TypeLengthInstanceValue process(final int index, final TypeLengthInstanceValue tliv) {
            return tliv;
        }

        @Override
        protected CreateSessionResponse internalBuild(final Gtp2MessageType type,
                                                      final Buffer buffer,
                                                      final Gtp2Header header,
                                                      final List<TypeLengthInstanceValue> tlivs) {
            return new CreateSessionResponse.CreateSessionResponseImpl(type, header, buffer, tlivs);
        }
    }

    class DeleteSessionResponseImpl extends ImmutableGtp2Response implements DeleteSessionResponse {

        protected DeleteSessionResponseImpl(final Gtp2MessageType type,
                                            final Gtp2Header header,
                                            final Buffer buffer,
                                            final List<TypeLengthInstanceValue> values) {
            super(type, header, buffer, values);
        }
    }
}
