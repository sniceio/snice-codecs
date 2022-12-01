package io.snice.codecs.codec.http;

/**
 * Represents framed HTTP Header.
 * <p>
 * Also, all standard HTTP header names have been defined and have been gratefully
 * copied from Netty's HttpHeaderNames, which also points out:
 *
 * <p>
 * These are all defined as lowercase to support HTTP/2 requirements while also not violating HTTP/1.x
 * requirements. New header names should always be lowercase
 * </p>
 * <p>
 * (Source: io.netty.handler.codec.http.HttpHeaderNames)
 *
 * @param <T>
 */
public interface HttpHeader<T> {

    /**
     * {@code "accept"}
     */
    String ACCEPT = "accept";

    /**
     * {@code "accept-charset"}
     */
    String ACCEPT_CHARSET = "accept-charset";
    /**
     * {@code "accept-encoding"}
     */
    String ACCEPT_ENCODING = "accept-encoding";
    /**
     * {@code "accept-language"}
     */
    String ACCEPT_LANGUAGE = "accept-language";
    /**
     * {@code "accept-ranges"}
     */
    String ACCEPT_RANGES = "accept-ranges";
    /**
     * {@code "accept-patch"}
     */
    String ACCEPT_PATCH = "accept-patch";
    /**
     * {@code "access-control-allow-credentials"}
     */
    String ACCESS_CONTROL_ALLOW_CREDENTIALS =
            "access-control-allow-credentials";
    /**
     * {@code "access-control-allow-headers"}
     */
    String ACCESS_CONTROL_ALLOW_HEADERS =
            "access-control-allow-headers";
    /**
     * {@code "access-control-allow-methods"}
     */
    String ACCESS_CONTROL_ALLOW_METHODS =
            "access-control-allow-methods";
    /**
     * {@code "access-control-allow-origin"}
     */
    String ACCESS_CONTROL_ALLOW_ORIGIN =
            "access-control-allow-origin";
    /**
     * {@code "access-control-expose-headers"}
     */
    String ACCESS_CONTROL_EXPOSE_HEADERS =
            "access-control-expose-headers";
    /**
     * {@code "access-control-max-age"}
     */
    String ACCESS_CONTROL_MAX_AGE = "access-control-max-age";
    /**
     * {@code "access-control-request-headers"}
     */
    String ACCESS_CONTROL_REQUEST_HEADERS =
            "access-control-request-headers";
    /**
     * {@code "access-control-request-method"}
     */
    String ACCESS_CONTROL_REQUEST_METHOD =
            "access-control-request-method";
    /**
     * {@code "age"}
     */
    String AGE = "age";
    /**
     * {@code "allow"}
     */
    String ALLOW = "allow";
    /**
     * {@code "authorization"}
     */
    String AUTHORIZATION = "authorization";
    /**
     * {@code "cache-control"}
     */
    String CACHE_CONTROL = "cache-control";
    /**
     * {@code "connection"}
     */
    String CONNECTION = "connection";
    /**
     * {@code "content-base"}
     */
    String CONTENT_BASE = "content-base";
    /**
     * {@code "content-encoding"}
     */
    String CONTENT_ENCODING = "content-encoding";
    /**
     * {@code "content-language"}
     */
    String CONTENT_LANGUAGE = "content-language";
    /**
     * {@code "content-length"}
     */
    String CONTENT_LENGTH = "content-length";
    /**
     * {@code "content-location"}
     */
    String CONTENT_LOCATION = "content-location";
    /**
     * {@code "content-transfer-encoding"}
     */
    String CONTENT_TRANSFER_ENCODING = "content-transfer-encoding";
    /**
     * {@code "content-disposition"}
     */
    String CONTENT_DISPOSITION = "content-disposition";
    /**
     * {@code "content-md5"}
     */
    String CONTENT_MD5 = "content-md5";
    /**
     * {@code "content-range"}
     */
    String CONTENT_RANGE = "content-range";
    /**
     * {@code "content-security-policy"}
     */
    String CONTENT_SECURITY_POLICY = "content-security-policy";
    /**
     * {@code "content-type"}
     */
    String CONTENT_TYPE = "content-type";
    /**
     * {@code "cookie"}
     */
    String COOKIE = "cookie";
    /**
     * {@code "date"}
     */
    String DATE = "date";
    /**
     * {@code "dnt"}
     */
    String DNT = "dnt";
    /**
     * {@code "etag"}
     */
    String ETAG = "etag";
    /**
     * {@code "expect"}
     */
    String EXPECT = "expect";
    /**
     * {@code "expires"}
     */
    String EXPIRES = "expires";
    /**
     * {@code "from"}
     */
    String FROM = "from";
    /**
     * {@code "host"}
     */
    String HOST = "host";
    /**
     * {@code "if-match"}
     */
    String IF_MATCH = "if-match";
    /**
     * {@code "if-modified-since"}
     */
    String IF_MODIFIED_SINCE = "if-modified-since";
    /**
     * {@code "if-none-match"}
     */
    String IF_NONE_MATCH = "if-none-match";
    /**
     * {@code "if-range"}
     */
    String IF_RANGE = "if-range";
    /**
     * {@code "if-unmodified-since"}
     */
    String IF_UNMODIFIED_SINCE = "if-unmodified-since";
    /**
     * @deprecated use {@link #CONNECTION}
     * <p>
     * {@code "keep-alive"}
     */
    @Deprecated
    String KEEP_ALIVE = "keep-alive";
    /**
     * {@code "last-modified"}
     */
    String LAST_MODIFIED = "last-modified";
    /**
     * {@code "location"}
     */
    String LOCATION = "location";
    /**
     * {@code "max-forwards"}
     */
    String MAX_FORWARDS = "max-forwards";
    /**
     * {@code "origin"}
     */
    String ORIGIN = "origin";
    /**
     * {@code "pragma"}
     */
    String PRAGMA = "pragma";
    /**
     * {@code "proxy-authenticate"}
     */
    String PROXY_AUTHENTICATE = "proxy-authenticate";
    /**
     * {@code "proxy-authorization"}
     */
    String PROXY_AUTHORIZATION = "proxy-authorization";
    /**
     * @deprecated use {@link #CONNECTION}
     * <p>
     * {@code "proxy-connection"}
     */
    @Deprecated
    String PROXY_CONNECTION = "proxy-connection";
    /**
     * {@code "range"}
     */
    String RANGE = "range";
    /**
     * {@code "referer"}
     */
    String REFERER = "referer";
    /**
     * {@code "retry-after"}
     */
    String RETRY_AFTER = "retry-after";
    /**
     * {@code "sec-websocket-key1"}
     */
    String SEC_WEBSOCKET_KEY1 = "sec-websocket-key1";
    /**
     * {@code "sec-websocket-key2"}
     */
    String SEC_WEBSOCKET_KEY2 = "sec-websocket-key2";
    /**
     * {@code "sec-websocket-location"}
     */
    String SEC_WEBSOCKET_LOCATION = "sec-websocket-location";
    /**
     * {@code "sec-websocket-origin"}
     */
    String SEC_WEBSOCKET_ORIGIN = "sec-websocket-origin";
    /**
     * {@code "sec-websocket-protocol"}
     */
    String SEC_WEBSOCKET_PROTOCOL = "sec-websocket-protocol";
    /**
     * {@code "sec-websocket-version"}
     */
    String SEC_WEBSOCKET_VERSION = "sec-websocket-version";
    /**
     * {@code "sec-websocket-key"}
     */
    String SEC_WEBSOCKET_KEY = "sec-websocket-key";
    /**
     * {@code "sec-websocket-accept"}
     */
    String SEC_WEBSOCKET_ACCEPT = "sec-websocket-accept";
    /**
     * {@code "sec-websocket-protocol"}
     */
    String SEC_WEBSOCKET_EXTENSIONS = "sec-websocket-extensions";
    /**
     * {@code "server"}
     */
    String SERVER = "server";
    /**
     * {@code "set-cookie"}
     */
    String SET_COOKIE = "set-cookie";
    /**
     * {@code "set-cookie2"}
     */
    String SET_COOKIE2 = "set-cookie2";
    /**
     * {@code "te"}
     */
    String TE = "te";
    /**
     * {@code "trailer"}
     */
    String TRAILER = "trailer";
    /**
     * {@code "transfer-encoding"}
     */
    String TRANSFER_ENCODING = "transfer-encoding";
    /**
     * {@code "upgrade"}
     */
    String UPGRADE = "upgrade";
    /**
     * {@code "upgrade-insecure-requests"}
     */
    String UPGRADE_INSECURE_REQUESTS = "upgrade-insecure-requests";
    /**
     * {@code "user-agent"}
     */
    String USER_AGENT = "user-agent";
    /**
     * {@code "vary"}
     */
    String VARY = "vary";
    /**
     * {@code "via"}
     */
    String VIA = "via";
    /**
     * {@code "warning"}
     */
    String WARNING = "warning";
    /**
     * {@code "websocket-location"}
     */
    String WEBSOCKET_LOCATION = "websocket-location";
    /**
     * {@code "websocket-origin"}
     */
    String WEBSOCKET_ORIGIN = "websocket-origin";
    /**
     * {@code "websocket-protocol"}
     */
    String WEBSOCKET_PROTOCOL = "websocket-protocol";
    /**
     * {@code "www-authenticate"}
     */
    String WWW_AUTHENTICATE = "www-authenticate";
    /**
     * {@code "x-frame-options"}
     */
    String X_FRAME_OPTIONS = "x-frame-options";
    /**
     * {@code "x-requested-with"}
     */
    String X_REQUESTED_WITH = "x-requested-with";

    String name();

    T value();
}
