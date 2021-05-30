package io.snice.codecs.codec.diameter.impl;

import io.snice.buffer.Buffer;
import io.snice.codecs.codec.diameter.DiameterHeader;
import io.snice.codecs.codec.diameter.DiameterMessage;
import io.snice.codecs.codec.diameter.DiameterParseException;
import io.snice.codecs.codec.diameter.avp.FramedAvp;
import io.snice.codecs.codec.diameter.avp.api.*;
import io.snice.functional.Either;
import io.snice.preconditions.ValidationError;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class ImmutableDiameterMessage implements DiameterMessage {

    /**
     * The full raw diameter message.
     */
    private final Buffer raw;

    protected final DiameterHeader header;
    private final List<FramedAvp> avps;

    private final short indexResultCode;
    private final short indexExperimentalResultCode;

    private final short indexOrigHost;
    private final short indexOrigRealm;

    private final short indexDestHost;
    private final short indexDestRealm;

    public ImmutableDiameterMessage(final Buffer raw,
                                    final DiameterHeader header,
                                    final List<FramedAvp> avps,
                                    final short indexOrigHost,
                                    final short indexOrigRealm,
                                    final short indexDestHost,
                                    final short indexDestRealm,
                                    final short indexResultCode,
                                    final short indexExperimentalResultCode) {
        this.raw = raw;
        this.header = header;
        this.avps = Collections.unmodifiableList(avps);
        this.indexOrigHost = indexOrigHost;
        this.indexOrigRealm = indexOrigRealm;
        this.indexDestHost = indexDestHost;
        this.indexDestRealm = indexDestRealm;
        this.indexResultCode = indexResultCode;
        this.indexExperimentalResultCode = indexExperimentalResultCode;
    }

    @Override
    public Optional<FramedAvp> getAvp(final long code) {
        return avps.stream().filter(avp -> avp.getCode() == code).findFirst();
    }

    @Override
    public DiameterHeader getHeader() {
        return header;
    }

    @Override
    public List<FramedAvp> getAllAvps() {
        return avps;
    }

    @Override
    public Buffer getBuffer() {
        return raw;
    }
    /**
     * This class is immutable and as such, when cloning, you'll just get back the same
     * reference again.
     *
     * @return
     */
    @Override
    public DiameterMessage clone() {
        return this;
    }

    @Override
    public OriginHost getOriginHost() {
        if (indexOrigHost == -1) {
            return null;
        }

        return (OriginHost)avps.get(indexOrigHost).ensure();
    }

    @Override
    public OriginRealm getOriginRealm() {
        if (indexOrigRealm == -1) {
            return null;
        }
        return (OriginRealm)avps.get(indexOrigRealm).ensure();
    }

    public Optional<DestinationRealm> getDestinationRealm() {
        if (indexDestRealm == -1) {
            return Optional.empty();
        }

        return Optional.of((DestinationRealm)avps.get(indexDestRealm).ensure());
    }

    public Optional<DestinationHost> getDestinationHost() {
        if (indexDestHost == -1) {
            return Optional.empty();
        }

        return Optional.of((DestinationHost)avps.get(indexDestHost).ensure());
    }

    protected Either<ExperimentalResult, ResultCode> getInternalResultCode() {
        if (indexResultCode != -1) {
            return Either.right((ResultCode) avps.get(indexResultCode));
        }

        if (indexExperimentalResultCode != -1) {
            return Either.left((ExperimentalResult)avps.get(indexExperimentalResultCode).ensure());
        }

        // TODO: what's the best approach here? Neither was supplied so throw exception?
        throw new DiameterParseException("No Result-Code or Experimental-Result present in Answer");
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(header.toString());
        sb.append(", AVP Count: ").append(avps.size());
        return sb.toString();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }

        try {
            return DiameterEquality.equals(this, (DiameterMessage)other);
        } catch (final ClassCastException e) {
            return false;
        }

    }


}
