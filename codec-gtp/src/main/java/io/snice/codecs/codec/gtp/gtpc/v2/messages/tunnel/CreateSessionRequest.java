package io.snice.codecs.codec.gtp.gtpc.v2.messages.tunnel;

import io.snice.buffer.Buffer;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Header;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Message;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2MessageType;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Request;
import io.snice.codecs.codec.gtp.gtpc.v2.Impl.*;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.*;

import java.util.List;
import java.util.Optional;

import static io.snice.preconditions.PreConditions.assertArgument;

public interface CreateSessionRequest extends Gtp2Request {

    static Gtp2MessageFramer<CreateSessionRequest> from(final Gtp2MessageType type, final Gtp2Header header, final Buffer buffer) {
        assertArgument(type == Gtp2MessageType.CREATE_SESSION_REQUEST);
        return new CreateSessionRequestFramer(type, header, buffer);
    }

    static Gtp2MessageBuilder<CreateSessionRequest> create() {
        return new CreateSessionRequestBuilder();
    }

    default Optional<BearerContext> getBearerContext() {
        return getInfoElement(BearerContext.TYPE, 0).map(tliv -> (BearerContext) (tliv.ensure()));
    }

    default Optional<FTeid> getFTeid(final int instance) {
        return getInfoElement(FTeid.TYPE, instance).map(tliv -> (FTeid) (tliv.ensure()));
    }

    default Optional<FTeid> getFTeid() {
        return getFTeid(0);
    }

    static Gtp2MessageBuilder<CreateSessionRequest> create(final Gtp2Header header) {
        assertArgument(header.getType() == Gtp2MessageType.CREATE_SESSION_REQUEST);
        return new CreateSessionRequestBuilder(header);
    }

    @Override
    default CreateSessionRequest toCreateSessionRequest() {
        return this;
    }

    @Override
    Gtp2MessageBuilder<CreateSessionResponse> createResponse();

    /**
     *
     */
    default Optional<FTeid> getSenderFTeid() {
        return getInfoElement(FTeid.TYPE, 0).map(f -> (FTeid) f.ensure());
    }

    @Override
    default Gtp2MessageType getType() {
        return Gtp2MessageType.CREATE_SESSION_REQUEST;
    }

    @Override
    default boolean isCreateSessionRequest() {
        return true;
    }

    class CreateSessionRequestFramer extends AbstractGtp2MessageFramer<CreateSessionRequest> {

        private Imsi imsi;
        private Msisdn msisdn;
        // done - M RAT Type
        // done - M Sender F-TEID for Control Plane - FTEID
        // done - M APN
        // M Bearer Contexts (grouped IE)
        // Bearer Contexts according to 29.274 Section 7.2.1-2
        //     - M EPI (EPS Bearer ID)
        //     - M Bearer Level QoS
        //     - M FTEID (S8-U SGW FTEID)

        // done - C MEI - ME Identity
        // done - C PDN Type

        // C/CO ULI - User Location Information
        // C/CO Service Network
        // C Indication Flags
        // C/CO Selection Mode
        // C/CO PDN Address Allocation (PAA)
        // C Maximum APN Restriction


        private CreateSessionRequestFramer(final Gtp2MessageType type, final Gtp2Header header, final Buffer buffer) {
            super(type, header, buffer);
        }

        @Override
        protected TypeLengthInstanceValue process(final int index, final TypeLengthInstanceValue tliv) {
            switch (tliv.getType()) {
                case Imsi.TYPE_VALUE:
                    imsi = (Imsi) tliv.ensure();
                    return imsi;
                case Msisdn.TYPE_VALUE:
                    msisdn = (Msisdn) tliv.ensure();
                    return msisdn;
                default:
                    return tliv;
            }
        }

        @Override
        protected CreateSessionRequest internalBuild(final Gtp2MessageType type, final Buffer buffer, final Gtp2Header header, final List<TypeLengthInstanceValue> tlivs) {
            return new CreateSessionRequestImpl(type, header, buffer, tlivs, imsi, msisdn);
        }
    }

    class CreateSessionRequestBuilder extends AbstractGtp2MessageBuilder<CreateSessionRequest> {

        private Imsi imsi;
        private Msisdn msisdn;

        private CreateSessionRequestBuilder() {
            super(Gtp2MessageType.CREATE_SESSION_REQUEST);
        }

        private CreateSessionRequestBuilder(final Gtp2Header header) {
            super(header);
        }

        @Override
        protected TypeLengthInstanceValue process(final int index, final TypeLengthInstanceValue tliv) {
            switch (tliv.getType()) {
                case Imsi.TYPE_VALUE:
                    imsi = (Imsi) tliv.ensure();
                    return imsi;
                case Msisdn.TYPE_VALUE:
                    msisdn = (Msisdn) tliv.ensure();
                    return msisdn;
                default:
                    return tliv;
            }
        }

        @Override
        protected CreateSessionRequest internalBuild(final Gtp2MessageType type, final Buffer buffer, final Gtp2Header header, final List<TypeLengthInstanceValue> tlivs) {
            return new CreateSessionRequestImpl(type, header, buffer, tlivs, imsi, msisdn);
        }

    }

    class CreateSessionRequestImpl extends ImmutableGtp2Request implements CreateSessionRequest {

        private final Optional<Imsi> imsi;
        private final Optional<Msisdn> msisdn;

        private CreateSessionRequestImpl(final Gtp2MessageType type,
                                         final Gtp2Header header,
                                         final Buffer body,
                                         final List<TypeLengthInstanceValue> values,
                                         final Imsi imsi,
                                         final Msisdn msisdn) {
            super(type, header, body, values);
            this.imsi = Optional.ofNullable(imsi);
            this.msisdn = Optional.ofNullable(msisdn);
        }

        @Override
        public Gtp2MessageBuilder<CreateSessionResponse> createResponse() {
            final var responseHeader = getHeader().toGtp2Header().copy()
                    .withType(Gtp2MessageType.CREATE_SESSION_RESPONSE)
                    .build();
            return CreateSessionResponse.create(responseHeader);
        }
    }
}
