package io.snice.codecs.codec.http;

import static io.snice.preconditions.PreConditions.assertNotNull;

public final class HttpProvider {

    /**
     * Note: this is technically not thread safe but the intent is that the implementation is configured
     * from the main thread before the rest of the program is initialized and as such, the variable will
     * be set.
     */
    private static HttpMessageFactory messageFactory;

    public static void setMessageFactory(final HttpMessageFactory messageFactory) {
        assertNotNull(messageFactory);
        HttpProvider.messageFactory = messageFactory;
    }

    public static HttpMessageFactory messageFactory() {
        if (messageFactory == null) {
            throw new IllegalStateException("No Http Provider has been specified");
        }
        return messageFactory;
    }
}
