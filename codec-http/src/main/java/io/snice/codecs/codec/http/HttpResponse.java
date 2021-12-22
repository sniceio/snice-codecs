package io.snice.codecs.codec.http;

import static io.snice.codecs.codec.http.HttpProvider.messageFactory;

public interface HttpResponse extends HttpMessage {

    static Builder<HttpResponse> create(final int statusCode) {
        return messageFactory().createResponse(statusCode);
    }

    static Builder<HttpResponse> create(final int statusCode, final String reasonPhrase) {
        return messageFactory().createResponse(statusCode, reasonPhrase);
    }

    int statusCode();

    String reasonPhrase();

    @Override
    default boolean isResponse() {
        return true;
    }

    @Override
    default HttpResponse toResponse() {
        return this;
    }
}
