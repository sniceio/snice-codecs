package io.snice.codecs.codec.diameter;

import io.snice.buffer.Buffer;
import io.snice.buffer.ReadableBuffer;
import io.snice.codecs.codec.diameter.impl.DiameterRequestBuilder;
import io.snice.codecs.codec.diameter.impl.ImmutableDiameterHeader;

/**
 *
 * @author jonas@jonasborjesson.com
 */
public interface DiameterHeader {

    /**
     * The only valid version is 1 and when framing a {@link DiameterHeader},
     * the framer will ensure that this is indeed true or else you cannot construct
     * a new header. If/when this changes in the future, simply override this method.
     *
     * @return
     */
    default int getVersion() {
        return 1;
    }

    int getLength();

    boolean isRequest();

    default boolean isAnswer() {
        return !isRequest();
    }

    boolean isProxiable();

    boolean isError();

    boolean isPossiblyRetransmission();

    int getCommandCode();

    long getApplicationId();

    long getHopByHopId();

    long getEndToEndId();

    /**
     * Get the underlying {@link Buffer} that serves as the byte-array backing storage.
     */
    Buffer getBuffer();

    /**
     * Create a copy of this {@link DiameterHeader} so it can be changed to suit
     * any changes you may need. E.g., if this {@link DiameterHeader} came in with
     * a DiameterRequest and you want to change the header to be that of
     * a DiameterAnswer then copy it and just mark it as an answer through
     * {@link Builder#isAnswer()} and then eventually you will also need to set the
     * length {@link Builder#withLength(int)} ()} but other than that, all the other
     * information such as application id, the hop-by-hop and end-to-end identifiers
     * will stay the same, which is what you want in the general case. But of course,
     * you're free to do whatever you want :-)
     *
     * @return a builder that allows you to modify the copy of this {@link DiameterHeader}.
     */
    Builder copy();

    /**
     * If you'd like to ensure that the header is indeed a proper header then there are a few
     * things we can check. E.g., the version must be set to one. The last 4 bits of the Command Flags
     * are reserved and not set per specification and the length must be a multiple of 4.
     *
     * @return
     */
    boolean validate();

    static DiameterHeader frame(final ReadableBuffer buffer) throws DiameterParseException {
        return ImmutableDiameterHeader.frame(buffer);
    }

    static DiameterHeader.Builder createCER() {
        final var header = createHeader(257);
        header.withApplicationId(0L);
        return header;
    }

    static DiameterHeader.Builder createULR() {
        final var header = createHeader(CommandCode.UpdateLocation.getCode());
        header.withApplicationId(16777251L);
        return header;
    }

    static DiameterHeader.Builder createAIR() {
        final var header = createHeader(CommandCode.AuthenticationInformation.getCode());
        header.withApplicationId(16777251L);
        return header;
    }

    static DiameterHeader.Builder createPUR() {
        final var header = createHeader(CommandCode.PurgeUe.getCode());
        header.withApplicationId(16777251L);
        return header;
    }

    static DiameterHeader.Builder createNOR() {
        final var header = createHeader(CommandCode.Notify.getCode());
        header.withApplicationId(16777251L);
        return header;
    }

    static DiameterHeader.Builder createHeader(final int commandCode) {
        final var header = DiameterHeader.of();
        header.withCommandCode(commandCode);
        header.isRequest();

        return header;
    }

    static Builder of() {
        return ImmutableDiameterHeader.of();
    }

    interface Builder {

        /**
         * Mark this as a request
         */
        Builder isRequest();

        /**
         * Mark this as an answer
         *
         * @return
         */
        Builder isAnswer();

        Builder withCommandCode(int code);

        Builder withApplicationId(long applicationId);

        Builder withEndToEndId(long id);

        Builder withHopToHopId(long id);

        /**
         * Set the total length of this, eventually one would assume, Diameter Message
         */
        Builder withLength(int length);

        DiameterHeader build();

        Builder copy();
    }

}
