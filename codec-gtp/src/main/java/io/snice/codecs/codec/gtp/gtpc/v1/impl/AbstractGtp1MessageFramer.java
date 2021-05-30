package io.snice.codecs.codec.gtp.gtpc.v1.impl;

import io.snice.buffer.Buffer;
import io.snice.codecs.codec.gtp.gtpc.InfoElement;
import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1Header;
import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1Message;
import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1MessageType;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGtp1MessageFramer<T extends Gtp1Message> implements Gtp1MessageFramer<T> {

    private final Gtp1MessageType type;

    private final Gtp1Header header;

    /**
     * The full framed GTPv1 message.
     */
    private final Buffer buffer;

    protected AbstractGtp1MessageFramer(final Gtp1MessageType type, final Gtp1Header header, final Buffer buffer) {
        this.type = type;
        this.header = header;
        this.buffer = buffer;
    }

    private List<InfoElement> processBody(final Buffer body) {
        Buffer values = body;
        int index = 0;
        final var ies = new ArrayList<InfoElement>(10);
        while (values.capacity() > 0) {
            final InfoElement ie = InfoElement.frame(values);
            ies.add(process(index, ie));
            values = values.slice(ie.getTotalSize(), values.capacity());
            ++index;
        }
        return ies;
    }

    /**
     * Ask the specific builder to process the given {@link InfoElement}. The reason is
     * different GTPv1 messages have different requirements on what needs to be present etc and may
     * also want to keep track of certain IEs for faster lookup etc. The specialized builder may
     * therefore decide to keep track of the index of a given {@link InfoElement} for
     * faster lookup.
     * <p>
     * Also, if e.g. a given {@link InfoElement} is important the builder may decide to fully
     * parse that IE right away by calling {@link InfoElement#ensure()} right away and return
     * that now fully parsed IE.
     *
     * @param index the index of where this IE will be stored within the internal list of IEs
     * @param ie    the {@link InfoElement} to be processed.
     * @return the processed {@link InfoElement}.
     */
    protected InfoElement process(final int index, final InfoElement ie) {
        return ie;
    }

    @Override
    public final T build() {
        // final var raw = buffer.slice(0, header.getTotalLength());
        // final var payload = buffer.slice(header.getHeaderLength(), header.getTotalLength());

        // if PDU, then the body is just the IP packet. If not, then there may be IEs present.
        final var body = buffer.slice(header.getHeaderLength(), header.getHeaderLength() + header.getBodyLength());
        final List<InfoElement> ies;
        final Buffer payload;
        if (header.getType() == Gtp1MessageType.G_PDU) {
            ies = List.of();
            payload = body;
        } else {
            ies = processBody(body);
            payload = null;
        }

        return internalBuild(type, buffer, ies, header, payload);
    }

    protected abstract T internalBuild(Gtp1MessageType type, Buffer buffer, List<InfoElement> ies, Gtp1Header header, Buffer payload);
}
