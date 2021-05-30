package io.snice.codecs.codec.gtp.gtpc.v1.impl;

import io.snice.buffer.Buffer;
import io.snice.codecs.codec.gtp.gtpc.InfoElement;
import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1Header;
import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1Message;
import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1MessageType;

import java.util.List;

public class GenericGtp1MessageBuilder<T extends Gtp1Message> extends AbstractGtp1MessageBuilder<T> {

    protected GenericGtp1MessageBuilder(final Gtp1MessageType type) {
        super(type);
    }

    protected GenericGtp1MessageBuilder(final Gtp1Header header) {
        super(header);
    }

    protected GenericGtp1MessageBuilder(final Gtp1MessageType type, final Gtp1Header header) {
        super(type, header);
    }

    @Override
    protected InfoElement process(final int index, final InfoElement ie) {
        return ie;
    }

    @Override
    protected T internalBuild(final Gtp1MessageType type, final Buffer buffer, final Gtp1Header header,
                              final List<InfoElement> ies, final Buffer payload) {
        // TODO: the gtp1 message type doesn't have if the message is a request or response.
        return (T) new ImmutableGtp1Request(type, header, buffer, ies, payload);
    }
}
