package io.snice.codecs.codec.http;

import java.net.URI;
import java.net.URISyntaxException;

import static io.snice.codecs.codec.http.HttpProvider.messageFactory;

public interface HttpRequest extends HttpMessage {

    static Builder<HttpRequest> create(final HttpMethod method, final URI uri) {
        return messageFactory().createRequest(method, uri);
    }

    static Builder<HttpRequest> get(final URI uri) {
        return messageFactory().createRequest(HttpMethod.GET, uri);
    }

    static Builder<HttpRequest> get(final String uri) {
        return get(uri(uri));
    }

    static Builder<HttpRequest> post(final URI uri) {
        return messageFactory().createRequest(HttpMethod.POST, uri);
    }

    static Builder<HttpRequest> post(final String uri) {
        return post(uri(uri));
    }

    static Builder<HttpRequest> delete(final URI uri) {
        return messageFactory().createRequest(HttpMethod.DELETE, uri);
    }

    static Builder<HttpRequest> delete(final String uri) {
        return delete(uri(uri));
    }

    static Builder<HttpRequest> put(final URI uri) {
        return messageFactory().createRequest(HttpMethod.PUT, uri);
    }

    static Builder<HttpRequest> put(final String uri) {
        return put(uri(uri));
    }

    private static URI uri(final String uri) {
        try {
            return new URI(uri);
        } catch (final URISyntaxException e) {
            throw new IllegalArgumentException("Illegal URI", e);
        }
    }

    /**
     * Check whether this request is to go over {@link HttpScheme#HTTPS}
     *
     * @return
     */
    default boolean isSecure() {
        return scheme() == HttpScheme.HTTPS;
    }

    HttpScheme scheme();

    HttpMethod method();

    HttpVersion version();

    URI uri();

    @Override
    default boolean isRequest() {
        return true;
    }

    @Override
    default HttpRequest toRequest() {
        return this;
    }
}
