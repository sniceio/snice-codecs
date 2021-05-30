package io.snice.codecs.codec.gtp.gtpc.v2.tliv;

import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Builder;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Message;
import io.snice.codecs.codec.gtp.gtpc.v2.Impl.Gtp2MessageBuilder;

/**
 * TS 29.274 section 8.28 - BearerContext
 * <p>
 * Specialized builder to aid in constructing a {@link BearerContext} and is meant to be used in conduction
 * with {@link Gtp2MessageBuilder} and as such, there is actually no builder method on it (the {@link Gtp2MessageBuilder}
 * will be the ones building it and adding it as a {@link TypeLengthInstanceValue} to the message).
 */
public interface BearerContextBuilder<T extends Gtp2Message> extends Gtp2Builder<T> {

    /**
     * TS 29.274 section 8.8 - EPS Bearer ID (EBI).
     *
     * @return
     */
    FTeidBuilder<T, BearerContextBuilder<T>> withNewSgwFTeid();

    /**
     * TS 29.274 section 8.8 - EPS Bearer ID (EBI).
     *
     * @param value the id and must be within 0-15 (inclusive)
     */
    BearerContextBuilder<T> withEpsBearerId(int value);

    /**
     * TS 29.274 section 8.15 - Bearer Quality of Service (Bearer QoS)
     *
     * @param qci the QCI value of the Bearer QoS
     * @return a {@link BearerQualityOfServiceBuilder} to aid constructing the {@link BearerQos} and once done
     * you can optionally call {@link BearerQualityOfServiceBuilder#doneBearerQoS()} to get back to this
     * {@link BearerContextBuilder} in order to continue building the {@link BearerContext}.
     */
    BearerQualityOfServiceBuilder<T> withNewBearerQualityOfService(int qci);

    /**
     * Optionally call this method when you are done constructing the {@link BearerContext} and want to "get back"
     * to the {@link Gtp2MessageBuilder} to continue building the {@link Gtp2Message}. This is just a
     * convenience method to get a fluid builder API.
     */
    Gtp2MessageBuilder<T> doneBearerContext();
}
