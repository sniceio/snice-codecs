package io.snice.codecs.codec.gtp.gtpc.v2;

import io.snice.buffer.Buffer;
import io.snice.codecs.codec.gtp.Teid;
import io.snice.codecs.codec.gtp.gtpc.v2.Impl.ImmutableGtp2Header;

public interface Gtp2HeaderBuilder {

    static Gtp2HeaderBuilder of(final Gtp2MessageType type) {
        return ImmutableGtp2Header.of(type);
    }

    Gtp2HeaderBuilder withType(byte type);

    Gtp2HeaderBuilder withType(Gtp2MessageType type);

    Gtp2HeaderBuilder withTeid(Teid tied);

    Gtp2HeaderBuilder withSequenceNumber(Buffer seqNo);

    /**
     * The size of the GTPv2 message is somewhat odd. It is the length
     * of all the TLIVs included in the message but excluding the mandatory
     * 4 bytes of the header. However, the TEID (if present) and the Seq No
     * shall be included in the size.
     * <p>
     * To make it easier, just count the size of the TLIVs and then we'll check
     * if the TEID is present, add that to the size and of course, add the size
     * of the seq no as well.
     *
     * @param size the size of all the TLIVs, in bytes.
     */
    Gtp2HeaderBuilder withTlivSize(int size);

    Gtp2Header build();
}
