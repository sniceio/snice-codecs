package io.snice.codecs.codec.diameter.impl;

import io.snice.buffer.Buffer;
import io.snice.codecs.codec.diameter.*;
import io.snice.codecs.codec.diameter.avp.FramedAvp;
import io.snice.codecs.codec.diameter.avp.api.ExperimentalResultCode;
import io.snice.codecs.codec.diameter.avp.api.OriginHost;
import io.snice.codecs.codec.diameter.avp.api.ResultCode;
import io.snice.codecs.codec.diameter.avp.api.SessionId;
import io.snice.functional.Either;

import java.util.List;

public class ImmutableDiameterRequest extends ImmutableDiameterMessage implements DiameterRequest {

    public ImmutableDiameterRequest(final Buffer raw,
                                    final DiameterHeader header,
                                    final List<FramedAvp> avps,
                                    final short indexOriginHost,
                                    final short indexOriginRealm,
                                    final short indexOfDestinationHost,
                                    final short indexOfDestinationRealm,
                                    final short indexResultCode,
                                    final short indexExperimentalResultCode) {
        super(raw, header, avps, indexOriginHost, indexOriginRealm, indexOfDestinationHost, indexOfDestinationRealm, indexResultCode, indexExperimentalResultCode);
    }

    @Override
    public final boolean isRequest() {
        return true;
    }

    @Override
    public final DiameterRequest toRequest() {
        return this;
    }

    @Override
    public DiameterAnswer.Builder createAnswer(final ResultCode resultCode) throws DiameterParseException, ClassCastException {
        final var builder = ImmutableDiameterAnswer.withResultCode(resultCode);
        return internalCreateAnswer(builder);
    }

    @Override
    public DiameterAnswer.Builder createAnswer(final ExperimentalResultCode resultCode) throws DiameterParseException, ClassCastException {
        final var builder = ImmutableDiameterAnswer.withResultCode(resultCode);
        return internalCreateAnswer(builder);
    }

    /**
     * Follows the RFC 6733 6.2. Diameter Answer Processing. Meaning:
     * <ul>
     *    <li>The same Hop-by-Hop Identifier in the request is used in the answer.</li>
     *    <li>The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message.</li>
     *    <li>The Result-Code AVP is added with its value indicating success or failure.</li>
     *    <li>If the Session-Id is present in the request, it MUST be included in the answer.</li>
     *    <li>Any Proxy-Info AVPs in the request MUST be added to the answer message, in the same order they were present in the request.</li>
     *    <li>The 'P' bit is set to the same value as the one in the request.</li>
     *    <li>The same End-to-End identifier in the request is used in the answer.</li>
     * </ul>
     *
     * Note that the RFC also states: "The local host's identity is encoded in the Origin-Host AVP"
     *
     * However, that will be up to the application to set that. This method will not do that
     * since it may not be clear what the {@link OriginHost} would be by just relying on
     * what's available in the {@link DiameterRequest} from which we are building this answer.
     */
    private DiameterAnswer.Builder internalCreateAnswer(DiameterAnswer.Builder builder) {
        final var header = super.header.copy().isAnswer();
        builder.withDiameterHeader(header);
        getAvp(SessionId.CODE).ifPresent(sessionId -> builder.withAvp(sessionId.ensure()));

        // TODO: any proxy-info avps should be copied as well.

        return builder;
    }

    @Override
    public DiameterRequest.Builder copy() {
        throw new RuntimeException("Not implemented just yet");
    }
}
