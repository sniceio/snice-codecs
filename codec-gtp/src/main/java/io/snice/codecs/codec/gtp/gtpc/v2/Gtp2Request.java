package io.snice.codecs.codec.gtp.gtpc.v2;

import io.snice.codecs.codec.gtp.gtpc.v2.Impl.Gtp2MessageBuilder;

public interface Gtp2Request extends Gtp2Message {

    @Override
    default boolean isRequest() {
        return true;
    }

    @Override
    default Gtp2Request toGtp2Request() {
        return this;
    }

    <T extends Gtp2Response> Gtp2MessageBuilder<T> createResponse();

}
