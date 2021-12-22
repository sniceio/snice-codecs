package io.snice.codecs.codec.http;

import io.snice.buffer.Buffer;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Note: unlike all other codecs in this project, the HTTP one
 * isn't actually implemented here. There are many HTTP implementations
 * in Java so no real reason to re-invent the wheel. However, occasionally
 * there is a need to change implementations but still maintain the interfaces, which
 * is why this package exists. As such, these are the common interfaces that
 * all of Snice projects are using and then the actual implementation will be
 * separated into e.g. a Netty based one etc.
 */
public interface HttpMessage {

    default boolean isRequest() {
        return false;
    }

    default boolean isResponse() {
        return false;
    }

    /**
     * Retrieve the first header of the given name. If there is many headers
     * with the same name, only the first will be returned. If there are none,
     * an empty {@link Optional} will be returned.
     */
    default <T> Optional<HttpHeader<T>> header(final String name) {
        return header((CharSequence) name);
    }

    <T> Optional<HttpHeader<T>> header(CharSequence name);

    /**
     * Retrieve all the headers with the specified name. If there are no
     * header with the given name, an empty list will be returned.
     */
    default <T> List<HttpHeader<T>> headers(final String name) {
        return headers((CharSequence) name);
    }

    <T> List<HttpHeader<T>> headers(CharSequence name);

    Optional<Buffer> content();

    /**
     * Retrieve all {@link HttpHeader}s in this {@link HttpMessage}.
     *
     * @return an immutable list of headers.
     */
    List<HttpHeader<?>> headers();

    default HttpRequest toRequest() {
        throw new ClassCastException("Unable to cast this " + getClass().getName()
                + " into a " + HttpRequest.class.getName());
    }

    default HttpResponse toResponse() {
        throw new ClassCastException("Unable to cast this " + getClass().getName()
                + " into a " + HttpResponse.class.getName());
    }

    interface Builder<T extends HttpMessage> {

        Builder<T> header(String name, String value);

        Builder<T> header(HttpHeader<?> header);

        /**
         * Add content to the {@link HttpMessage}. You should also add the Content-Type
         * so that the remote server knows how to interpret the content.
         * <p>
         * By default, a Content-Length header will be added to the message corresponding
         * to the length of the given content buffer. If you do not wish for this to happen, call
         * the {@link #noDefaults()} method.
         * <p>
         * Note: calling any of the <code>content</code> methods on this builder will result in a multi-mime
         * message.
         *
         * @param content the content to add as a body to the resulting {@link HttpMessage}.
         */
        Builder<T> content(Buffer content);

        /**
         * Often you want to send some form-encoded data with your {@link HttpMessage} and instead of formatting
         * a set of key-values manually, simply supply them as a {@link Map} here, and they will be automatically
         * form-encoded when the message is sent.
         * <p>
         * By default, a Content-Type header of "application/x-www-form-urlencoded" will automatically be
         * added (as well as a Content-Length header). If you do not wish to have this behavior, call the
         * {@link #noDefaults()} method and then you are in full control yourself.
         * <p>
         * Note that if a value is of type {@link List} then the default behavior is to add every single element
         * in that list as a separate form-encoded "entry". I.e., if the given {@link Map} contains an entry:
         *
         * <code>
         * final var content = Map.of("my_key", List.of("one", "two", "three"));
         * builder.content(content);
         * </code>
         * <p>
         * Then the resulting HTTP message would contain a form-encoded body where "my_key" would be represented
         * as such:
         *
         * <code>
         * my_key=one&my_key=two&my_key=three
         * </code>
         * <p>
         * Note that this "expansion" only happens at the top level so a list of lists would not "explode"
         * the given key <code>M*N</code> entries.
         *
         * <p>
         * Note: calling any of the <code>content</code> methods on this builder will result in a multi-mime
         * message.
         *
         * @param content the content to add as a form-encoded body to the resulting {@link HttpMessage}.
         * @return
         */
        Builder<T> content(Map<String, ? extends Object> content);

        /**
         * Basic Http Authorization.
         *
         * @param username
         * @param password
         */
        Builder<T> auth(String username, String password);

        /**
         * There are a number of default behaviors that the {@link Builder} will perform, such as
         * automatically adding a Host header if a host was present in the target {@link URI}
         * when this builder was created through {@link HttpMessageFactory#createRequest(HttpMethod, URI)}.
         * <p>
         * If that behavior is no desirable, call this method and all default behaviors will be turned off.
         * <p>
         * Default behaviors (which will be turned off if you call this method):
         * <ul>
         *     <li>If HTTP request, set the HTTP version to 1.1, if not manually overridden</li>
         *     <li>If HTTP request and version is HTTP 1.1, set the <code>Connection</code> header to <code>Close</code></li>
         *     <li>Add Host header if host was present in the target {@link URI}</li>
         *     <li>Add Content-Length header if {@link #content(Buffer)} has been added to the message</li>
         * </ul>
         *
         * @return
         */
        Builder<T> noDefaults();

        Builder<T> version(HttpVersion version);

        T build();
    }
}
