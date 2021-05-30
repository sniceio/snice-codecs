package io.snice.codecs.codec.gtp.gtpc.v1;

import io.snice.buffer.Buffer;
import io.snice.codecs.codec.gtp.Teid;

public interface Gtp1HeaderBuilder {

    Gtp1HeaderBuilder withTeid(Teid tied);

    Gtp1HeaderBuilder withTeid(Buffer tied);

    Gtp1HeaderBuilder withSequenceNumber(Buffer seqNo);

    Gtp1HeaderBuilder withType(Gtp1MessageType type);

    /**
     * The size of the GTPv1 message is somewhat odd. It is the length
     * of all the TLVs included (or the raw payload in case of G-PDU message)
     * in the message but excluding the mandatory 4 bytes of the header.
     * However, if some of the optional header values are included, then that
     * is included in the length.
     * <p>
     * To make it easier, just count the size of the TLVs (or raw body) and then we'll check
     * if the optional header values are present, add that to the size.
     *
     * @param size the size of all the TLIVs, in bytes.
     */
    Gtp1HeaderBuilder withBodySize(int size);

    Gtp1Header build();
}
