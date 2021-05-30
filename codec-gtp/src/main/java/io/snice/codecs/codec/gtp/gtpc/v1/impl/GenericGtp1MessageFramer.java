package io.snice.codecs.codec.gtp.gtpc.v1.impl;

import io.snice.buffer.Buffer;
import io.snice.codecs.codec.gtp.gtpc.InfoElement;
import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1Header;
import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1Message;
import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1MessageType;

import java.util.List;

public class GenericGtp1MessageFramer<T extends Gtp1Message> extends AbstractGtp1MessageFramer<T> {

    protected GenericGtp1MessageFramer(final Gtp1MessageType type, final Gtp1Header header, final Buffer buffer) {
        super(type, header, buffer);
    }

    @Override
    protected T internalBuild(final Gtp1MessageType type, final Buffer buffer, final List<InfoElement> ies, final Gtp1Header header, final Buffer payload) {
        return (T) new ImmutableGtp1Message(type, header, buffer, ies, payload);
    }
}
