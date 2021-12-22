package io.snice.codecs.codec.http;

import java.net.URI;

public interface HttpMessageFactory {

    /**
     * Create a new {@link HttpRequest} {@link HttpMessage.Builder}. The given target will be
     * parsed out where the components of the {@link URI} are treated as follows:
     * <ul>
     *     <li>Scheme - if specified, it MUST be either HTTP or HTTPS. Anything else is an error.</li>
     *     <li>Scheme - if not specified, it will default to HTTP</li>
     *     <li>Host - if specified, the host will be included in the HTTP 1.1. request as a Host header.
     *     For HTTP 1.0, it will not be included since the Host header is not mandatory in HTTP 1.0.
     *     However, if you wish to NOT include it (perhaps you are testing a server so it checks the existence
     *     of the Host header) then either do not include it in the target URI or tell the builder to
     *     turn off its default behavior by calling {@link HttpMessage.Builder#noDefaults()}.
     *     </li>
     *     <li></li>
     *     <li></li>
     * </ul>
     *
     * @param method
     * @param target
     * @return
     */
    HttpMessage.Builder<HttpRequest> createRequest(HttpMethod method, URI target);

    /**
     * Create a {@link HttpResponse} with the given status code;
     *
     * @param status
     * @return
     */
    HttpMessage.Builder<HttpResponse> createResponse(int status);

    HttpMessage.Builder<HttpResponse> createResponse(int status, String reasonPhrase);
}
