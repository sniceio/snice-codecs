package io.snice.codecs.codec.gtp.gtpc.v1.impl;

import io.snice.buffer.Buffer;
import io.snice.codecs.codec.gtp.gtpc.InfoElement;
import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1Header;
import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1MessageType;
import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1Response;

import java.util.List;

public class ImmutableGtp1Response extends ImmutableGtp1Message implements Gtp1Response {

    protected ImmutableGtp1Response(final Gtp1MessageType type, final Gtp1Header header,
                                    final Buffer buffer,
                                    final List<InfoElement> ies,
                                    final Buffer payload) {
        super(type, header, buffer, ies, payload);
    }
}
