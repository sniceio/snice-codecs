package io.snice.codecs.codec.http;

import io.snice.buffer.Buffer;

public interface HttpBody {

    /**
     * Get the raw byte-array representing this {@link HttpBody} wrapped as a {@link Buffer}.
     */
    Buffer content();

    /**
     * Get the Content-Type representing this body.
     */
    HttpHeader<String> contentType();

    static FormUrlEncodedBuilder formUrlEncoded() {
        return null;
    }

    interface Builder {
        HttpBody build();
    }

    interface FormUrlEncodedBuilder extends Builder {
        FormUrlEncodedBuilder param(String name, Object value);
    }
    

}
