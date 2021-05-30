package io.snice.codecs.codec.gtp.gtpc.v1;

import io.snice.codecs.codec.gtp.gtpc.v1.messages.path.EchoRequest;
import io.snice.codecs.codec.gtp.gtpc.v1.messages.path.EchoResponse;

public interface Gtp1Request extends Gtp1Message {

    default EchoRequest toEchoRequest() {
        throw new ClassCastException("Unable to cast a " + getClass().getName() + " into a " + EchoRequest.class.getName());
    }

    default EchoResponse toEchoResponse() {
        throw new ClassCastException("Unable to cast a " + getClass().getName() + " into a " + EchoResponse.class.getName());
    }

    @Override
    default boolean isRequest() {
        return true;
    }

    @Override
    default Gtp1Request toGtp1Request() {
        return this;
    }

}
