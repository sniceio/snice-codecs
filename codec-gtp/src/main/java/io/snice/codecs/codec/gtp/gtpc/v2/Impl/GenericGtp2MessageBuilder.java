package io.snice.codecs.codec.gtp.gtpc.v2.Impl;

import io.snice.buffer.Buffer;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Header;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Message;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2MessageType;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.TypeLengthInstanceValue;

import java.util.List;

public class GenericGtp2MessageBuilder<T extends Gtp2Message> extends AbstractGtp2MessageBuilder<T> {

    protected GenericGtp2MessageBuilder(final Gtp2MessageType type) {
        super(type);
    }

    protected GenericGtp2MessageBuilder(final Gtp2Header header) {
        super(header);
    }

    @Override
    protected TypeLengthInstanceValue process(final int index, final TypeLengthInstanceValue tliv) {
        return tliv;
    }

    @Override
    protected T internalBuild(final Gtp2MessageType type, final Buffer buffer, final Gtp2Header header, final List<TypeLengthInstanceValue> tlivs) {
        if (type.isRequest()) {
            return (T) new ImmutableGtp2Request(type, header, buffer, tlivs);
        } else {
            return (T) new ImmutableGtp2Response(type, header, buffer, tlivs);
        }
    }

}
