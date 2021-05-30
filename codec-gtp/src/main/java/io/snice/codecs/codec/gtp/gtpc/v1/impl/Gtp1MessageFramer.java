package io.snice.codecs.codec.gtp.gtpc.v1.impl;

import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1Message;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Message;

/**
 * Generic interface for all various types of {@link Gtp1Message} framers.
 * <p>
 * Note that unlike builders, the framers assume we have read the entire
 * GTP message off of e.g. the network. You would use the various builders
 * when you want to construct a new GTP message from scratch, or modify
 * an existing one.
 *
 * @param <T>
 */
public interface Gtp1MessageFramer<T extends Gtp1Message> {

    T build();
}
