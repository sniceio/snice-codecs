package io.snice.codecs.codec.gtp.gtpc.v1;

public interface Gtp1Response extends Gtp1Message {

    @Override
    default boolean isResponse() {
        return true;
    }

    @Override
    default Gtp1Response toGtp1Response() {
        return this;
    }

}
