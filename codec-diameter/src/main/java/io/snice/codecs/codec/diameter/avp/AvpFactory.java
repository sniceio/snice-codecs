package io.snice.codecs.codec.diameter.avp;

import io.snice.codecs.codec.diameter.avp.api.*;
import io.snice.codecs.codec.diameter.avp.type.*;

public class AvpFactory {

    public static <T extends DiameterType> Avp<T> create(final Class<Avp<T>> cls, final T value) {
        return null;
    }

    private static Avp<UTF8String> create(final Class<Avp<UTF8String>> cls, final UTF8String value) {
        final int code = AvpReflection.getCode(cls);
        switch (code) {
            case ApnOiReplacement.CODE:
                return ApnOiReplacement.of(value);
            default:
                throw new RuntimeException("oooops");
        }
    }

    private static Avp<OctetString> create(final Class<Avp<OctetString>> cls, final OctetString value) {
        final int code = AvpReflection.getCode(cls);
        switch (code) {
            case VisitedPlmnId.CODE:
                return VisitedPlmnId.of(value);
            default:
                throw new RuntimeException("oooops");
        }
    }

    private static Avp<IpAddress> create(final Class<Avp<IpAddress>> cls, final IpAddress value) {
        final int code = AvpReflection.getCode(cls);
        switch (code) {
            case HostIpAddress.CODE:
                return HostIpAddress.of(value);
            default:
                throw new RuntimeException("oooops");
        }
    }

    private static Avp<Time> create(final Class<Avp<Time>> cls, final Time value) {
        final int code = AvpReflection.getCode(cls);
        switch (code) {
            case ExpirationDate.CODE:
                return ExpirationDate.of(value);
            default:
                throw new RuntimeException("oooops");
        }
    }

    private static Avp<Integer32> create(final Class<Avp<Integer32>> cls, final Integer64 value) {
        final int code = AvpReflection.getCode(cls);
        switch (code) {
            default:
                throw new RuntimeException("oooops");
        }
    }

    private static Avp<Unsigned32> create(final Class<Avp<Unsigned32>> cls,  final Unsigned32 value) {
        final int code = AvpReflection.getCode(cls);
        switch (code) {
            case DsaFlags.CODE:
                return DsaFlags.of(value);

            default:
                throw new RuntimeException("oooops");
        }
    }
}
