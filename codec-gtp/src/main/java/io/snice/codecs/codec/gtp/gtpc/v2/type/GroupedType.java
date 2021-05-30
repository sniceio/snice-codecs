package io.snice.codecs.codec.gtp.gtpc.v2.type;

import io.snice.buffer.Buffer;
import io.snice.buffer.Buffers;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2InfoElement;
import io.snice.codecs.codec.gtp.gtpc.v2.Impl.Utils;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.BearerQos;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.FTeid;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.TypeLengthInstanceValue;
import io.snice.codecs.codec.gtp.gtpc.v2.type.impl.ImmutableGtpType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static io.snice.preconditions.PreConditions.assertArgument;

/**
 *
 */
public interface GroupedType extends GtpType {

    static GroupedType parse(final Buffer buffer) {
        final var readable = buffer.toReadableBuffer();
        final var list = new ArrayList<TypeLengthInstanceValue<? extends GtpType>>();
        while (readable.hasReadableBytes()) {
            final var ie = TypeLengthInstanceValue.frame(readable);
            list.add(ie);
        }
        return new DefaultGroupedType(buffer, Collections.unmodifiableList(list));
    }

    static GroupedType parse(final String buffer) {
        return parse(Buffers.wrap(buffer));
    }

    static GroupedType ofValue(final Buffer buffer) {
        return parse(buffer);
    }

    static GroupedType ofValue(final String apn) {
        return parse(apn);
    }

    static GroupedType ofValue(final TypeLengthInstanceValue<? extends GtpType>... ies) {
        assertArgument(ies != null && ies.length > 0, "You must specify at least one IE");
        final var buffers = new Buffer[ies.length];
        for (int i = 0; i < ies.length; ++i) {
            buffers[i] = ies[i].getRaw();
        }
        final var buffer = Buffers.wrap(buffers);
        return new DefaultGroupedType(buffer, List.of(ies));
    }

    List<TypeLengthInstanceValue<? extends GtpType>> getInfoElements();

    Optional<? extends TypeLengthInstanceValue> getInfoElement(final Gtp2InfoElement type);

    Optional<? extends TypeLengthInstanceValue> getInfoElement(final Gtp2InfoElement type, final int instance);

    default Optional<BearerQos> getBearerQos() {
        return getInfoElement(BearerQos.TYPE).map(v -> (BearerQos)v.ensure());
    }

    /**
     * Note that this will fetch the first {@link FTeid} that is found irrespective
     * of its instance number.
     *
     * @return
     */
    default Optional<FTeid> getBearerFTeid() {
        return getInfoElement(FTeid.TYPE).map(v -> (FTeid) v.ensure());
    }

    class DefaultGroupedType extends ImmutableGtpType<GroupedType> implements GroupedType {
        private final List<TypeLengthInstanceValue<? extends GtpType>> ies;

        protected DefaultGroupedType(final Buffer buffer, final List<TypeLengthInstanceValue<? extends GtpType>> ies) {
            super(buffer);
            this.ies = ies;
        }

        @Override
        public List<TypeLengthInstanceValue<? extends GtpType>> getInfoElements() {
            return ies;
        }

        @Override
        public Optional<? extends TypeLengthInstanceValue> getInfoElement(final Gtp2InfoElement type) {
            return Utils.getInformationElement(type, ies);
        }

        @Override
        public Optional<? extends TypeLengthInstanceValue> getInfoElement(final Gtp2InfoElement type, final int instance) {
            return Utils.getInformationElement(type, instance, ies);
        }
    }
}
