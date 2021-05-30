package io.snice.codecs.codec.gtp.gtpc.v2.Impl;

import io.snice.buffer.Buffer;
import io.snice.buffer.ReadableBuffer;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Header;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Message;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2MessageType;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.TypeLengthInstanceValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractGtp2MessageFramer<T extends Gtp2Message> implements Gtp2MessageFramer<T> {

    private final List<TypeLengthInstanceValue> tlivs = new ArrayList<>();

    private final Gtp2MessageType type;

    private final Gtp2Header header;

    /**
     * The full framed GTPv2 message.
     */
    private final Buffer buffer;

    protected AbstractGtp2MessageFramer(final Gtp2MessageType type, final Gtp2Header header, final Buffer buffer) {
        this.type = type;
        this.header = header;
        this.buffer = buffer;
    }

    private void processBody() {
        final ReadableBuffer values = buffer.slice(header.getHeaderLength(), header.getHeaderLength() + header.getBodyLength()).toReadableBuffer();
        int index = 0;
        while (values.hasReadableBytes()) {
            final TypeLengthInstanceValue tliv = TypeLengthInstanceValue.frame(values);
            tlivs.add(process(index, tliv));
            ++index;
        }
    }

    /**
     * Ask the specific builder to process the given {@link TypeLengthInstanceValue}. The reason is
     * different GTPv2 messages have different requirements on what needs to be present etc and may
     * also want to keep track of certain TLIVs for faster lookup etc. The specialized builder may
     * therefore decide to keep track of the index of a given {@link TypeLengthInstanceValue} for
     * faster lookup.
     * <p>
     * Also, if e.g. a given {@link TypeLengthInstanceValue} is important the builder may decide to fully
     * parse that TLIV right away by calling {@link TypeLengthInstanceValue#ensure()} right away and return
     * that now fully parsed TLIV.
     *
     * @param index the index of where this TLIV will be stored within the internal list of TLIVs
     * @param tliv  the {@link TypeLengthInstanceValue} to be processed.
     * @return the processed {@link TypeLengthInstanceValue}.
     */
    protected TypeLengthInstanceValue process(final int index, final TypeLengthInstanceValue tliv) {
        return tliv;
    }

    @Override
    public final T build() {
        processBody();
        return internalBuild(type, buffer, header, Collections.unmodifiableList(tlivs));
    }

    protected abstract T internalBuild(Gtp2MessageType type, Buffer buffer, Gtp2Header header, List<TypeLengthInstanceValue> tlivs);
}
