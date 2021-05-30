package io.snice.codecs.codec.diameter;


import io.snice.codecs.codec.diameter.impl.DiameterRequestBuilder;
import io.snice.preconditions.PreConditions;

import static io.snice.preconditions.PreConditions.assertArgument;
import static io.snice.preconditions.PreConditions.assertNotNull;

/**
 * @author jonas@jonasborjesson.com
 */
public interface DiameterRequest extends DiameterMessage {

    static Builder createCER() {
        return DiameterRequestBuilder.createCER();
    }

    static Builder createULR() {
        return DiameterRequestBuilder.createULR();
    }

    static DiameterRequestBuilder createRequest(final int commandCode) {
        return DiameterRequestBuilder.createRequest(commandCode);
    }

    static DiameterRequestBuilder createRequest(final DiameterHeader header) {
        assertNotNull(header, "The diameter header cannot be null if you want to create a request");
        return DiameterRequestBuilder.createRequest(header.copy());
    }

    static DiameterRequestBuilder createRequest(final DiameterHeader.Builder header) {
        return DiameterRequestBuilder.createRequest(header);
    }


    interface Builder extends DiameterMessage.Builder<DiameterRequest> {

        @Override
        default boolean isDiameterRequestBuilder() {
            return true;
        }

        @Override
        default DiameterMessage.Builder<DiameterRequest> toDiameterRequestBuilder() {
            return this;
        }

    }
}
