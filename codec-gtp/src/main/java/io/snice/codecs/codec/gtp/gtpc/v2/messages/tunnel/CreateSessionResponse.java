package io.snice.codecs.codec.gtp.gtpc.v2.messages.tunnel;

import io.snice.buffer.Buffer;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Header;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2MessageType;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Response;
import io.snice.codecs.codec.gtp.gtpc.v2.Impl.*;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.TypeLengthInstanceValue;

import java.util.List;

import static io.snice.codecs.codec.gtp.gtpc.v2.Gtp2MessageType.CREATE_SESSION_RESPONSE;
import static io.snice.preconditions.PreConditions.assertArgument;

public interface CreateSessionResponse extends Gtp2Response {

    static Gtp2MessageFramer<CreateSessionResponse> from(final Gtp2MessageType type, final Gtp2Header header, final Buffer buffer) {
        assertArgument(type == CREATE_SESSION_RESPONSE);
        return new CreateSessionResponseFramer(type, header, buffer);
    }

    static Gtp2MessageBuilder<CreateSessionResponse> create(final Gtp2Header header) {
        assertArgument(header.getType() == CREATE_SESSION_RESPONSE);
        return new CreateSessionResponseBuilder(header);
    }

    static Gtp2MessageBuilder<CreateSessionResponse> create() {
        return new CreateSessionResponseBuilder();
    }

    class CreateSessionResponseFramer extends AbstractGtp2MessageFramer<CreateSessionResponse> {

        private CreateSessionResponseFramer(final Gtp2MessageType type, final Gtp2Header header, final Buffer buffer) {
            super(type, header, buffer);
        }

        @Override
        protected CreateSessionResponse internalBuild(final Gtp2MessageType type, final Buffer buffer, final Gtp2Header header, final List<TypeLengthInstanceValue> tlivs) {
            return new CreateSessionResponseImpl(type, header, buffer, tlivs);
        }

    }

    class CreateSessionResponseBuilder extends AbstractGtp2MessageBuilder<CreateSessionResponse> {
        protected CreateSessionResponseBuilder() {
            super(CREATE_SESSION_RESPONSE);
        }

        protected CreateSessionResponseBuilder(final Gtp2Header header) {
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
            return new CreateSessionResponseImpl(type, header, buffer, tlivs);
        }
    }

    class CreateSessionResponseImpl extends ImmutableGtp2Response implements CreateSessionResponse {

        protected CreateSessionResponseImpl(final Gtp2MessageType type,
                                            final Gtp2Header header,
                                            final Buffer buffer,
                                            final List<TypeLengthInstanceValue> values) {
            super(type, header, buffer, values);
        }
    }
}
