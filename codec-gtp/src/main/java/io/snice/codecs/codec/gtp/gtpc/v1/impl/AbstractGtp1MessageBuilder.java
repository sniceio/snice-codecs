package io.snice.codecs.codec.gtp.gtpc.v1.impl;

import io.snice.buffer.Buffer;
import io.snice.buffer.Buffers;
import io.snice.codecs.codec.gtp.Teid;
import io.snice.codecs.codec.gtp.gtpc.InfoElement;
import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1Header;
import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1Message;
import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1MessageType;

import java.util.ArrayList;
import java.util.List;

import static io.snice.buffer.Buffers.assertNotEmpty;
import static io.snice.preconditions.PreConditions.assertArgument;
import static io.snice.preconditions.PreConditions.assertNotNull;
import static io.snice.preconditions.PreConditions.assertNull;

public abstract class AbstractGtp1MessageBuilder<T extends Gtp1Message> implements Gtp1MessageBuilder<T> {

    private Gtp1Header header;
    private boolean hasBeenBuilt = false;
    private final Gtp1MessageType type;
    private Teid teid;
    private Buffer seqNo;
    private Buffer payload;
    private int index;
    private final List<InfoElement> ies = new ArrayList<>(10); // default is 10 but making is super obvious

    /**
     * This is the total size of all IE & any eventual "body" (in the case of a PDU).
     * This value will have to be added into the {@link Gtp1Header}.
     */
    private int bodySize;

    protected AbstractGtp1MessageBuilder(final Gtp1MessageType type) {
        this.type = type;
    }

    protected AbstractGtp1MessageBuilder(final Gtp1Header header) {
        this.type = Gtp1MessageType.lookup(header.getMessageTypeDecimal());
        this.header = header;
    }

    protected AbstractGtp1MessageBuilder(final Gtp1MessageType type, final Gtp1Header header) {
        this.type = type;
        this.header = header;
    }

    @Override
    public final Gtp1MessageBuilder<T> withInfoElement(final InfoElement ie) {
        assertNotNull(ie, "The given Information Element cannot be null");
        final var processed = process(index, ie);
        ies.add(processed);
        bodySize += processed.getTotalSize();

        ++index;
        return this;
    }

    protected abstract InfoElement process(int index, InfoElement ie);

    @Override
    public Gtp1MessageBuilder<T> withTeid(final Buffer teid) {
        this.teid = Teid.of(teid);
        return this;
    }

    @Override
    public Gtp1MessageBuilder<T> withTeid(final Teid teid) {
        assertNotNull(teid, "The TEID cannot be null");
        this.teid = teid;
        return this;
    }

    @Override
    public Gtp1MessageBuilder<T> withSeqNo(final Buffer seqNo) {
        this.seqNo = seqNo;
        return this;
    }

    @Override
    public Gtp1MessageBuilder<T> withRandomSeqNo() {
        this.seqNo = Buffers.random(2);
        return this;
    }

    @Override
    public Gtp1MessageBuilder<T> withPayload(final Buffer buffer) {
        assertArgument(type == Gtp1MessageType.G_PDU, "You can only specify a payload for G-PDU messages");
        assertNotEmpty(buffer, "The payload cannot be empty");

        // TODO: unit test for this because we assume this is true and bodySize will be wrong if this changes
        assertNull(this.payload, "You have already set the payload once. You cannot set it again");

        this.payload = buffer;
        bodySize += buffer.capacity();
        return this;
    }

    @Override
    public final T build() {
        if (hasBeenBuilt) {
            throw new IllegalArgumentException("This GTP message has already been built once");
        }
        hasBeenBuilt = true;

        // if we have the header, we have a teid. If not, then it must be set.
        if (header == null) {
            assertNotNull(teid, "The TEID is not optional. You must specify it");
        }

        // assertArgument(type == Gtp1MessageType.G_PDU, "Sorry, can only do G-PDU messages right now");
        // assertNotEmpty(payload, "Since I can only do G-PDU messages right now, you also have to specify the payload");
        final var header = createHeader();
        final var buffer = payload != null ? Buffers.wrap(header.getBuffer(), payload) : header.getBuffer();
        return internalBuild(type, buffer, header, ies, payload);
    }

    private Gtp1Header createHeader() {
        final var header = this.header != null ? this.header.copy() : Gtp1Header.of(type);
        if (teid != null) {
            header.withTeid(teid);
        }

        if (seqNo != null) {
            header.withSequenceNumber(seqNo);
        }

        header.withBodySize(bodySize);

        return header.build();
    }

    protected abstract T internalBuild(final Gtp1MessageType type, final Buffer buffer, final Gtp1Header header,
                                       final List<InfoElement> ies, final Buffer payload);
}
