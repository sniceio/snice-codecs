package io.snice.codecs.codec.gtp.gtpc.v2.Impl;

import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Message;

/**
 * Generic interface for all various types of {@link Gtp2Message} framers.
 * <p>
 * Note that unlike builders, the framers assume we have read the entire
 * GTP message off of e.g. the network. You would use the various builders
 * when you want to construct a new GTP message from scratch, or modify
 * an existing one.
 *
 * @param <T>
 */
public interface Gtp2MessageFramer<T extends Gtp2Message> {

    T build();
}
