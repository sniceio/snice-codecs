package io.snice.codecs.codec.gtp.gtpc.v2;

public interface Gtp2Response extends Gtp2Message {

    @Override
    default boolean isResponse() {
        return true;
    }

    @Override
    default Gtp2Response toGtp2Response() {
        return this;
    }

}
