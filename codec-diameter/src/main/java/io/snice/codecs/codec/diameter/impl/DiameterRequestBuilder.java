package io.snice.codecs.codec.diameter.impl;

import io.snice.buffer.Buffer;
import io.snice.codecs.codec.diameter.DiameterHeader;
import io.snice.codecs.codec.diameter.DiameterMessage;
import io.snice.codecs.codec.diameter.DiameterRequest;
import io.snice.codecs.codec.diameter.avp.FramedAvp;

import java.util.List;

import static io.snice.preconditions.PreConditions.assertArgument;
import static io.snice.preconditions.PreConditions.assertNotNull;

public class DiameterRequestBuilder extends DiameterMessageBuilder<DiameterRequest> implements DiameterRequest.Builder {

    private DiameterRequestBuilder(final DiameterHeader.Builder header) {
        super(header);
    }

    @Override
    protected DiameterMessageBuilder<DiameterRequest> internalCopy(DiameterHeader.Builder header) {
        return DiameterRequest.createRequest(header);
    }

    public static DiameterRequestBuilder createCER() {
        return new DiameterRequestBuilder(DiameterHeader.createCER());
    }

    public static DiameterRequestBuilder createULR() {
        return new DiameterRequestBuilder(DiameterHeader.createULR());
    }

    public static DiameterRequestBuilder createRequest(final int commandCode) {
        return new DiameterRequestBuilder(DiameterHeader.createHeader(commandCode));
    }

    public static DiameterRequestBuilder createRequest(final DiameterHeader.Builder header) {
        assertNotNull(header, "The diameter header cannot be null if you want to create a request");
        header.isRequest();
        return new DiameterRequestBuilder(header);
    }

    @Override
    protected DiameterRequest internalBuild(final Buffer message, final DiameterHeader header,
                                            final List<FramedAvp> avps, final short indexOfOriginHost,
                                            final short indexOfOriginRealm, final short indexOfDestinationHost,
                                            final short indexOfDestinationRealm, final short indexResultCode,
                                            final short indexExperimentalResultCode) {
        return new ImmutableDiameterRequest(message, header, avps, indexOfOriginHost, indexOfOriginRealm,
                indexOfDestinationHost, indexOfDestinationRealm, indexResultCode, indexExperimentalResultCode);
    }
}
