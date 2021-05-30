package io.snice.codecs.codec.gtp.gtpc.v1.impl;

import io.snice.buffer.Buffer;
import io.snice.codecs.codec.gtp.GtpParseException;
import io.snice.codecs.codec.gtp.Teid;
import io.snice.codecs.codec.gtp.gtpc.InfoElement;
import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1Header;
import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1Message;
import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1MessageType;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Header;

/**
 * Generic interface for all various types of {@link Gtp1Message} builders.
 * <p>
 * Builders are used when you want to create a new {@link Gtp1Message} from scratch,
 * or to modify an existing one.
 *
 * @param <T>
 */
public interface Gtp1MessageBuilder<T extends Gtp1Message> {

    /**
     * Specify the {@link Teid} of the message (part of the {@link Gtp1Header}
     */
    Gtp1MessageBuilder<T> withTeid(Teid teid);
    Gtp1MessageBuilder<T> withTeid(Buffer teid);

    /**
     * Specify the sequence number of the message (part of the {@link Gtp1Header})
     */
    Gtp1MessageBuilder<T> withSeqNo(Buffer seqNo);

    /**
     * Have the builder generate a random sequence number, which will be
     * inserted into the {@link Gtp2Header}
     */
    Gtp1MessageBuilder<T> withRandomSeqNo();

    /**
     * Specify the raw payload.
     * <p>
     * Only valid for {@link Gtp1MessageType#G_PDU}
     *
     * @param buffer
     * @return
     */
    Gtp1MessageBuilder<T> withPayload(Buffer buffer);

    /**
     * Add an {@link InfoElement} to this message.
     */
    Gtp1MessageBuilder<T> withInfoElement(final InfoElement ie);

    /**
     * Build the {@link Gtp1Message}
     *
     * @throws IllegalArgumentException in case this message has already been built.
     * @throws GtpParseException        in case anything has not been constructed properly.
     */
    T build() throws IllegalArgumentException, GtpParseException;
}
