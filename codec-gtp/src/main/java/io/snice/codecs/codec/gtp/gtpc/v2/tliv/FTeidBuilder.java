package io.snice.codecs.codec.gtp.gtpc.v2.tliv;

import io.snice.codecs.codec.gtp.Teid;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Builder;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Message;
import io.snice.codecs.codec.gtp.gtpc.v2.Impl.Gtp2MessageBuilder;

public interface FTeidBuilder<T extends Gtp2Message, B extends Gtp2Builder<T>> {

    /**
     * Ask the builder to generate a random {@link Teid}
     */
    FTeidBuilder<T, B> withRandomizedTeid();

    FTeidBuilder<T, B> withTeid(Teid teid);

    FTeidBuilder<T, B> withIPv4Address(String iPv4Address);

    /**
     * Optionally call this method when you are done constructing the {@link FTeid} and want to "get back"
     * to the {@link Gtp2MessageBuilder} to continue building the {@link Gtp2Message}. This is just a
     * convenience method to get a fluid builder API.
     */
    B doneFTeid();
}
