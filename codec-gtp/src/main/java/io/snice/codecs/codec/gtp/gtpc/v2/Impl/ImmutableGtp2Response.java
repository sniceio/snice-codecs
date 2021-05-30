package io.snice.codecs.codec.gtp.gtpc.v2.Impl;

import io.snice.buffer.Buffer;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Header;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2MessageType;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Response;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.TypeLengthInstanceValue;

import java.util.List;

public class ImmutableGtp2Response extends ImmutableGtp2Message implements Gtp2Response {

    protected ImmutableGtp2Response(final Gtp2MessageType type, final Gtp2Header header,
                                    final Buffer buffer, final List<TypeLengthInstanceValue> values) {
        super(type, header, buffer, values);
    }
}
