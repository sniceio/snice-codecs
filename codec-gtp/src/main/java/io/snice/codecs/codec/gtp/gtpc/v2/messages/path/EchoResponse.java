package io.snice.codecs.codec.gtp.gtpc.v2.messages.path;

import io.snice.buffer.Buffer;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Header;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Message;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2MessageType;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Response;
import io.snice.codecs.codec.gtp.gtpc.v2.Impl.*;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.Recovery;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.TypeLengthInstanceValue;

import java.util.List;

import static io.snice.codecs.codec.gtp.gtpc.v2.Gtp2MessageType.ECHO_RESPONSE;
import static io.snice.preconditions.PreConditions.assertArgument;

public interface EchoResponse extends Gtp2Response {

    static Gtp2MessageFramer<EchoResponse> from(final Gtp2MessageType type, final Gtp2Header header, final Buffer buffer) {
        assertArgument(type == ECHO_RESPONSE);
        return new EchoResponseFramer(type, header, buffer);
    }

    static Gtp2MessageBuilder<EchoResponse> create(final Gtp2Header header) {
        assertArgument(header.getType() == ECHO_RESPONSE);
        return new EchoResponseBuilder<>(header);
    }

    static Gtp2MessageBuilder<EchoResponse> create() {
        return new EchoResponseBuilder<>();
    }

    Recovery getRecovery();

    @Override
    default Gtp2MessageType getType() {
        return ECHO_RESPONSE;
    }

    @Override
    default boolean isEchoResponse() {
        return true;
    }

    class EchoResponseFramer extends AbstractGtp2MessageFramer<EchoResponse> {

        /**
         * Recovery TLIV is mandatory
         */
        private Recovery recovery;

        private EchoResponseFramer(final Gtp2MessageType type, final Gtp2Header header, final Buffer buffer) {
            super(type, header, buffer);
        }

        @Override
        protected TypeLengthInstanceValue process(final int index, final TypeLengthInstanceValue tliv) {
            if (tliv.getType() == Recovery.TYPE.getType()) {
                recovery = (Recovery) tliv.ensure();
                return recovery;
            }
            return tliv;
        }

        @Override
        protected EchoResponse internalBuild(final Gtp2MessageType type, final Buffer buffer, final Gtp2Header header, final List<TypeLengthInstanceValue> tlivs) {
            return new EchoResponseImpl(type, header, buffer, tlivs, recovery);
        }
    }

    class EchoResponseBuilder<T extends Gtp2Message> extends AbstractGtp2MessageBuilder<T> {

        /**
         * Recovery TLIV is mandatory
         */
        private Recovery recovery;

        protected EchoResponseBuilder() {
            super(ECHO_RESPONSE);
        }

        protected EchoResponseBuilder(final Gtp2Header header) {
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
        protected T internalBuild(final Gtp2MessageType type, final Buffer buffer, final Gtp2Header header, final List<TypeLengthInstanceValue> tlivs) {
            return (T) new EchoResponseImpl(type, header, buffer, tlivs, recovery);
        }
    }

    class EchoResponseImpl extends ImmutableGtp2Response implements EchoResponse {

        private final Recovery recovery;

        private EchoResponseImpl(final Gtp2MessageType type,
                                 final Gtp2Header header,
                                 final Buffer body,
                                 final List<TypeLengthInstanceValue> values,
                                 final Recovery recovery) {
            super(type, header, body, values);
            this.recovery = recovery;
        }

        @Override
        public Recovery getRecovery() {
            return recovery;
        }

    }

}
