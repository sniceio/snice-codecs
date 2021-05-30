package io.snice.codecs.codec.diameter.impl;

import io.snice.buffer.Buffer;
import io.snice.buffer.WritableBuffer;
import io.snice.codecs.codec.diameter.DiameterHeader;
import io.snice.codecs.codec.diameter.DiameterMessage;
import io.snice.codecs.codec.diameter.DiameterRequest;
import io.snice.codecs.codec.diameter.avp.Avp;
import io.snice.codecs.codec.diameter.avp.FramedAvp;
import io.snice.codecs.codec.diameter.avp.api.DestinationHost;
import io.snice.codecs.codec.diameter.avp.api.DestinationRealm;
import io.snice.codecs.codec.diameter.avp.api.ExperimentalResult;
import io.snice.codecs.codec.diameter.avp.api.ExperimentalResultCode;
import io.snice.codecs.codec.diameter.avp.api.OriginHost;
import io.snice.codecs.codec.diameter.avp.api.OriginRealm;
import io.snice.codecs.codec.diameter.avp.api.ResultCode;
import io.snice.codecs.codec.diameter.avp.api.SessionId;
import io.snice.codecs.codec.diameter.avp.api.UserName;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static io.snice.preconditions.PreConditions.assertNotNull;
import static io.snice.preconditions.PreConditions.ensureNotNull;

public abstract class DiameterMessageBuilder<T extends DiameterMessage> implements DiameterMessage.Builder<T> {

    private DiameterHeader.Builder header;

    /**
     * These are all the AVPs that the user has added to this builder.
     * These AVPs may have been added to this list through any of the
     * withXXX-methods or they could have been copied from the
     * template if one was used.
     */
    private final List<Avp> avps;

    private Function<Avp, Avp> onAvpFunction;

    private short indexOfResultCode = -1;
    private short indexOfExperimentalResult = -1;
    private short indexOfOriginHost = -1;
    private short indexOfOriginRealm = -1;
    private short indexOfDestinationHost = -1;
    private short indexOfDestinationRealm = -1;
    private short indexOfUserName = -1;
    private short indexOfSessionId = -1;

    protected DiameterMessageBuilder(final int avpSizeHint, final DiameterHeader.Builder header) {
        this(avpSizeHint, header, null, null);
    }

    /**
     * Constructor for Answer
     *
     */
    protected DiameterMessageBuilder(final int avpSizeHint, final DiameterHeader.Builder header,
                                     final ResultCode resultCode, final ExperimentalResult experimentalResult) {
        avps = new ArrayList<>(avpSizeHint);
        if (resultCode != null) {
            indexOfResultCode = 0;
            avps.add(resultCode);
        }

        if (experimentalResult != null) {
            indexOfExperimentalResult = 0;
            avps.add(experimentalResult);
        }
        this.header = header;
    }

    protected DiameterMessageBuilder(final int avpSizeHint) {
        this(avpSizeHint, null);
    }

    protected DiameterMessageBuilder(final ResultCode resultCode) {
        this(10, null, resultCode, null);
    }

    protected DiameterMessageBuilder(final ExperimentalResultCode resultCode) {
        this(10, null, null, ExperimentalResultHelper.map(resultCode));
    }

    protected DiameterMessageBuilder() {
        this(10);
    }

    protected DiameterMessageBuilder(final DiameterHeader.Builder header) {
        this(10, header);
    }

    @Override
    public DiameterMessage.Builder<T> onAvp(final Function<Avp, Avp> f) throws IllegalStateException {
        if (this.onAvpFunction == null) {
            this.onAvpFunction = f;
        } else {
            this.onAvpFunction = this.onAvpFunction.andThen(f);
        }
        return this;
    }

    @Override
    public DiameterMessage.Builder<T> withDiameterHeader(final DiameterHeader.Builder header) {
        this.header = header;
        return this;
    }

    @Override
    public DiameterMessage.Builder<T> copy() {
        final var headerCopy = header.copy();
        final var builderCopy = internalCopy(headerCopy);

        builderCopy.indexOfResultCode = indexOfResultCode;
        builderCopy.indexOfExperimentalResult = indexOfExperimentalResult;
        builderCopy.indexOfOriginHost = indexOfOriginHost;
        builderCopy.indexOfOriginRealm = indexOfOriginRealm;
        builderCopy.indexOfDestinationHost = indexOfDestinationHost;
        builderCopy.indexOfDestinationRealm = indexOfDestinationRealm;
        builderCopy.indexOfUserName = indexOfUserName;
        builderCopy.indexOfSessionId = indexOfSessionId;

        builderCopy.avps.addAll(avps);
        builderCopy.onAvpFunction = onAvpFunction;

        return builderCopy;
    }

    protected abstract DiameterMessageBuilder<T> internalCopy(DiameterHeader.Builder header);

    // ----------------------------------------------------------------------
    // -------------------------- Origin Host -------------------------------
    // ----------------------------------------------------------------------

    @Override
    public DiameterMessage.Builder<T> withOriginHost(final Buffer originHost) {
        indexOfOriginHost = addTrackedAvp(indexOfOriginHost, OriginHost.of(originHost));
        return this;
    }

    @Override
    public DiameterMessage.Builder<T> withOriginHost(final String originHost) {
        indexOfOriginHost = addTrackedAvp(indexOfOriginHost, OriginHost.of(originHost));
        return this;
    }

    @Override
    public DiameterMessage.Builder<T> withOriginHost(final OriginHost originHost) {
        if (originHost != null) {
            indexOfOriginHost = addTrackedAvp(indexOfOriginHost, originHost);
        }
        return this;
    }

    // ----------------------------------------------------------------------
    // ------------------------- Origin Realm -------------------------------
    // ----------------------------------------------------------------------

    @Override
    public DiameterMessage.Builder<T> withOriginRealm(final Buffer originRealm) {
        indexOfOriginRealm = addTrackedAvp(indexOfOriginRealm, OriginRealm.of(originRealm));
        return this;
    }

    @Override
    public DiameterMessage.Builder<T> withOriginRealm(final String originRealm) {
        indexOfOriginRealm = addTrackedAvp(indexOfOriginRealm, OriginRealm.of(originRealm));
        return this;
    }

    @Override
    public DiameterMessage.Builder<T> withOriginRealm(final OriginRealm originRealm) {
        assertNotNull(originRealm, "The OriginRealm cannot be null");
        indexOfOriginRealm = addTrackedAvp(indexOfOriginRealm, originRealm);
        return this;
    }

    // ----------------------------------------------------------------------
    // ----------------------- Destination Host -----------------------------
    // ----------------------------------------------------------------------

    @Override
    public DiameterMessage.Builder<T> withDestinationHost(final DestinationHost destHost) {
        assertNotNull(destHost, "The DestinationHost cannot be null");
        indexOfDestinationHost = addTrackedAvp(indexOfDestinationHost, destHost);
        return this;
    }

    @Override
    public DiameterMessage.Builder<T> withDestinationHost(final String destHost) {
        indexOfDestinationHost = addTrackedAvp(indexOfDestinationHost, DestinationHost.of(destHost));
        return this;
    }

    @Override
    public DiameterMessage.Builder<T> withDestinationHost(final Buffer destHost) {
        indexOfDestinationHost = addTrackedAvp(indexOfDestinationHost, DestinationHost.of(destHost));
        return this;
    }

    // ----------------------------------------------------------------------
    // ---------------------- Destination Realm -----------------------------
    // ----------------------------------------------------------------------

    @Override
    public DiameterMessage.Builder<T> withDestinationRealm(final Buffer destRealm) {
        indexOfDestinationRealm = addTrackedAvp(indexOfDestinationRealm, DestinationRealm.of(destRealm));
        return this;
    }

    @Override
    public DiameterMessage.Builder<T> withDestinationRealm(final String destRealm) {
        indexOfDestinationRealm = addTrackedAvp(indexOfDestinationRealm, DestinationRealm.of(destRealm));
        return this;
    }

    @Override
    public DiameterMessage.Builder<T> withDestinationRealm(final DestinationRealm destRealm) {
        assertNotNull(destRealm, "The DestinationRealm cannot be null");
        indexOfDestinationRealm = addTrackedAvp(indexOfDestinationRealm, destRealm);
        return this;
    }

    // ----------------------------------------------------------------------
    // --------------------------- User Name --------------------------------
    // ----------------------------------------------------------------------

    @Override
    public DiameterMessage.Builder<T> withUserName(final UserName userName) {
        assertNotNull(userName, "The UserName cannot be null");
        indexOfUserName = addTrackedAvp(indexOfUserName, userName);
        return this;
    }

    @Override
    public DiameterMessage.Builder<T> withUserName(final String userName) {
        indexOfUserName = addTrackedAvp(indexOfUserName, UserName.of(userName));
        return this;
    }

    @Override
    public DiameterMessage.Builder<T> withUserName(final Buffer userName) {
        indexOfUserName = addTrackedAvp(indexOfUserName, UserName.of(userName));
        return this;
    }

    // ----------------------------------------------------------------------
    // -------------------------- Session Id --------------------------------
    // ----------------------------------------------------------------------

    @Override
    public DiameterMessage.Builder<T> withSessionId(final SessionId sessionId) {
        assertNotNull(sessionId, "The SessionId cannot be null");
        indexOfSessionId = addTrackedAvp(indexOfSessionId, sessionId);
        return this;
    }

    @Override
    public DiameterMessage.Builder<T> withSessionId(final String sessionId) {
        indexOfSessionId = addTrackedAvp(indexOfSessionId, SessionId.of(sessionId));
        return this;
    }

    @Override
    public DiameterMessage.Builder<T> withSessionId(final Buffer sessionId) {
        indexOfSessionId = addTrackedAvp(indexOfSessionId, SessionId.of(sessionId));
        return this;
    }

    @Override
    public DiameterMessage.Builder<T> withAvp(final Avp avp) {
        if (avp != null) {
            processAvp(avp);
        }
        return this;
    }

    @Override
    public DiameterMessage.Builder<T> onCommit(final Consumer<DiameterMessage> f) {
        return this;
    }

    @Override
    public T build() {
        ensureDiameterHeader();

        // the diameter message header is 20 bytes so we'll start there.
        int msgSize = 20;

        // TODO: following my SIP implementation here and I'm not sure if in Diameter
        // there are AVPs that has to be grouped together etc and if so, we have to deal
        // with in the same way. See the SipMessageBuilder in pkts.io (soon to be copied
        // to this project too)
        final int avpCount = avps.size();
        final List<FramedAvp> finalAvps = new ArrayList<>(avpCount);

        // Note, the avps.size() makes no sense at this point since we just did avpCount above.
        // However, there is a reason for this and has to do with how SIP works and since i'm just
        // copying that concept, I'm keeping it as is and then we'll see if in Diameter there are
        // AVPs, like e.g. the Via-headers in SIP, that must be grouped
        for (int i = 0; i < avps.size(); ++i) {
            final Avp avp = avps.get(i);

            if (avp != null) {
                final Avp finalAvp = processFinalAvp((short) finalAvps.size(), avp);
                if (finalAvp != null) {
                    // padding is not part of the length field according to spec so need to add it to the
                    // overall buffer size since we will be writing zeros into those padded slots.
                    // See RFC 6733 section 4
                    msgSize += avp.getLength() + avp.getPadding();
                    finalAvps.add(finalAvp);
                }
            }
        }

        // TODO: instead of copying over, create a composite buffer
        final WritableBuffer writable = WritableBuffer.of(msgSize);
        header.withLength(msgSize);
        final DiameterHeader finalHeader = header.build();
        finalHeader.getBuffer().writeTo(writable);
        finalAvps.forEach(avp -> avp.writeTo(writable));

        final Buffer buffer = writable.build();

        return internalBuild(buffer, finalHeader, finalAvps, indexOfOriginHost, indexOfOriginRealm,
                indexOfDestinationHost, indexOfDestinationRealm, indexOfResultCode, indexOfExperimentalResult);
    }

    /**
     * we must figure out a {@link DiameterHeader} and if the user hasn't
     * specified one we may be able to come up with one given enough information
     * has been supplied but if not, then we'll complain.
     */
    private void ensureDiameterHeader() {
        ensureNotNull(header, "You must specify the Diameter Header, either the builder or the actual header");
    }

    private Avp processFinalAvp(final short index, final Avp avp) {
        final Avp finalAvp;

        if (avp.isOriginHost()) {
            indexOfOriginHost = index;
            finalAvp = processGenericAvp(avp);
        } else {
            finalAvp = processGenericAvp(avp);
        }

        return finalAvp;
    }

    private Avp processGenericAvp(final Avp avp) {
        if (this.onAvpFunction != null) {
            return this.onAvpFunction.apply(avp);
        }

        return avp;
    }

    private void processAvp(final Avp avp) {
        if (avp.getCode() == OriginHost.CODE) {
            indexOfOriginHost = addTrackedAvp(indexOfOriginHost, avp);
        } else if (avp.getCode() == OriginRealm.CODE) {
            indexOfOriginRealm = addTrackedAvp(indexOfOriginRealm, avp);
        } else {
            addAvp(avp);
        }
    }

    private short addTrackedAvp(final short index, final Avp avp) {
        if (index != -1) {
            avps.set(index, avp.ensure());
            return index;
        }
        return addAvp(avp.ensure());
    }

    private short addAvp(final Avp avp) {
        avps.add(avp);
        return (short) (avps.size() - 1);
    }

    protected abstract T internalBuild(final Buffer message,
                                       final DiameterHeader header,
                                       final List<FramedAvp> avps,
                                       short indexOfOriginHost,
                                       short indexOfOriginRealm,
                                       short indexOfDestinationHos,
                                       short indexOfDestinationRealm,
                                       short indexResultCode,
                                       short indexOfExperimentalResultCode);
}
