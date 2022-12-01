package io.snice.codecs.codec.http;

import io.snice.buffer.Buffer;

import java.util.List;
import java.util.Optional;

public class DummyHttpMessage implements HttpMessage {


    @Override
    public <T> Optional<HttpHeader<T>> header(final String name) {
        return Optional.empty();
    }

    @Override
    public <T> Optional<HttpHeader<T>> header(final CharSequence name) {
        return Optional.empty();
    }

    @Override
    public <T> List<HttpHeader<T>> headers(final String name) {
        return null;
    }

    @Override
    public <T> List<HttpHeader<T>> headers(final CharSequence name) {
        return null;
    }

    @Override
    public Optional<Buffer> content() {
        return Optional.empty();
    }

    @Override
    public List<HttpHeader<?>> headers() {
        return null;
    }
}
