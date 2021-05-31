package io.snice.codecs.codec.gtp.gtpc.v2.Impl;

import io.snice.buffer.Buffer;
import io.snice.buffer.Buffers;
import io.snice.buffer.WritableBuffer;
import io.snice.codecs.codec.gtp.Teid;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Builder;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Header;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Message;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2MessageType;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.Ambr;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.Apn;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.ApnRestriction;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.BearerContext;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.BearerContextBuilder;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.BearerQos;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.BearerQualityOfServiceBuilder;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.Ebi;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.FTeid;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.FTeidBuilder;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.Imsi;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.Mei;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.Msisdn;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.Paa;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.Pdn;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.Rat;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.SelectionMode;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.ServingNetwork;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.TypeLengthInstanceValue;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.UeTimeZone;
import io.snice.codecs.codec.gtp.type.AmbrType;
import io.snice.codecs.codec.gtp.type.ArpType;
import io.snice.codecs.codec.gtp.type.BearerQosType;
import io.snice.codecs.codec.gtp.type.CounterType;
import io.snice.codecs.codec.gtp.type.EbiType;
import io.snice.codecs.codec.gtp.type.FTeidType;
import io.snice.codecs.codec.gtp.type.GroupedType;
import io.snice.codecs.codec.gtp.type.MccMncType;
import io.snice.codecs.codec.gtp.type.PaaType;
import io.snice.codecs.codec.gtp.type.PdnType;
import io.snice.codecs.codec.gtp.type.QosType;
import io.snice.codecs.codec.gtp.type.RatType;
import io.snice.codecs.codec.gtp.type.SelectionModeType;
import io.snice.codecs.codec.gtp.type.TbcdType;
import io.snice.codecs.codec.tgpp.ReferencePoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.snice.preconditions.PreConditions.assertNotEmpty;
import static io.snice.preconditions.PreConditions.assertNotNull;
import static io.snice.preconditions.PreConditions.assertNull;

public abstract class AbstractGtp2MessageBuilder<T extends Gtp2Message> implements Gtp2MessageBuilder<T> {

    private boolean hasBeenBuilt = false;
    private int index = 0;
    private final List<TypeLengthInstanceValue> tlivs = new ArrayList<>(10); // default is 10 but making is super obvious
    private final Gtp2MessageType type;
    private Teid teid;
    private Buffer seqNo;
    private int tlivSize;

    /**
     * The builder allows the user to set the PAA IPv4 and IPv6 address independently
     * and not until build time we know whether both are included and as such, if the
     * PDN Type is IPv4 only, IPv6 only or both (currently don't support the Non-IP
     * flag)
     */
    private String paaIPv4Address;
    private String paaIPv6Address;

    /**
     * TODO: you can actually have more than one.
     * <p>
     * There can be both bearer contexts to be created as well as deleted.
     * Also, there can be many of them but for now, we'll only allow
     * one.
     */
    private BearerContextBuilderImpl<T> bearerContextToBeCreated;

    private FTeidBuilderImpl<T, Gtp2MessageBuilder<T>> senderFTeidToBeCreated;

    /**
     * If a full header is supplied. Note, if e.g. TEID is supplied as well, we'll re-write
     * that portion of the header. This is true for any value that would otherwise
     * change the header.
     */
    private Gtp2Header header;

    protected AbstractGtp2MessageBuilder(final Gtp2MessageType type) {
        this.type = type;
    }

    protected AbstractGtp2MessageBuilder(final Gtp2Header header) {
        this.type = Gtp2MessageType.lookup(header.getMessageTypeDecimal());
        this.header = header;
    }

    @Override
    public Gtp2MessageBuilder<T> withHeader(final Gtp2Header header) {
        this.header = header;
        return this;
    }

    @Override
    public Gtp2MessageBuilder<T> withTeid(final Buffer teid) {
        this.teid = Teid.of(teid);
        return this;
    }

    @Override
    public Gtp2MessageBuilder<T> withSeqNo(final Buffer seqNo) {
        this.seqNo = seqNo;
        return this;
    }

    @Override
    public Gtp2MessageBuilder<T> withRandomSeqNo() {
        this.seqNo = Buffers.random(3);
        return this;
    }

    @Override
    public Gtp2MessageBuilder<T> withTeid(final Teid teid) {
        assertNotNull(teid, "The TEID cannot be null");
        this.teid = teid;
        return this;
    }

    @Override
    public final Gtp2MessageBuilder<T> withTliv(final TypeLengthInstanceValue tliv) {
        assertNotNull(tliv, "The given TypeLengthInstanceValue cannot be null");
        final var processed = process(index, tliv);
        tlivs.add(processed);
        tlivSize += processed.getTotalSize();

        ++index;
        return this;
    }

    @Override
    public Gtp2MessageBuilder<T> withRat(final int rat) {
        return withTliv(Rat.ofValue(RatType.of(rat)));
    }

    @Override
    public Gtp2MessageBuilder<T> withRat(final RatType rat) {
        return withTliv(Rat.ofValue(rat));
    }

    @Override
    public Gtp2MessageBuilder<T> withServingNetwork(final String mccMnc) {
        return withTliv(ServingNetwork.ofValue(MccMncType.ofValue(mccMnc)));
    }

    @Override
    public FTeidBuilder<T, Gtp2MessageBuilder<T>> withNewSenderControlPlaneFTeid() {
        assertNull(senderFTeidToBeCreated, "A Sender FTeid has already been specified");
        senderFTeidToBeCreated = new FTeidBuilderImpl<>(this, 0, ReferencePoint.S5, true);
        return senderFTeidToBeCreated;
    }

    @Override
    public BearerContextBuilder<T> withNewBearerContext() {
        final BearerContextBuilder<T> builder;
        if (bearerContextToBeCreated == null) {
            bearerContextToBeCreated = new BearerContextBuilderImpl<>(this);
            builder = bearerContextToBeCreated;
        } else {
            // TODO: the many case...
            throw new IllegalArgumentException("Currently only support a single Bearer Context");
        }

        return builder;
    }

    @Override
    public Gtp2MessageBuilder<T> withPdnType(final PdnType.Type type) {
        assertNotNull(type, "The PDN Type cannot be null");
        return withTliv(Pdn.ofValue(PdnType.of(type)));
    }

    @Override
    public Gtp2MessageBuilder<T> withImsi(final String imsi) {
        return withTliv(Imsi.ofValue(imsi));
    }

    @Override
    public Gtp2MessageBuilder<T> withImsi(final Imsi imsi) {
        assertNotNull(imsi, "The IMSI cannot be null");
        return withTliv(imsi);
    }

    @Override
    public Gtp2MessageBuilder<T> withApn(final String apn) {
        return withTliv(Apn.ofValue(apn));
    }

    @Override
    public Gtp2MessageBuilder<T> withMobileEquipmentIdentity(final String imei) {
        assertNotEmpty(imei, "The IMEI cannot be null or the empty string");
        return withTliv(Mei.ofValue(TbcdType.parse(imei)));
    }

    @Override
    public Gtp2MessageBuilder<T> withMsisdn(final String msisdn) {
        return withTliv(Msisdn.ofValue(msisdn));
    }

    @Override
    public Gtp2MessageBuilder<T> withIPv4PdnAddressAllocation(final String ipv4Address) {
        assertNotEmpty(ipv4Address, "The IPv4 address cannot be null or the empty string");
        this.paaIPv4Address = ipv4Address;
        return this;
    }

    @Override
    public Gtp2MessageBuilder<T> withIPv6PdnAddressAllocation(final String ipv6Address) {
        assertNotEmpty(ipv6Address, "The IPv6 address cannot be null or the empty string");
        this.paaIPv6Address = ipv6Address;
        return this;
    }

    @Override
    public Gtp2MessageBuilder<T> withUeTimeZone(final UeTimeZone tz) {
        // TODO: make more user friendly version of this
        // UeTimeZone.ofValue(Buffers.wrap((byte) 0x08, (byte) 0x00));
        assertNotNull(tz, "The UE Time Zone cannot be null");
        return withTliv(tz);
    }

    @Override
    public Gtp2MessageBuilder<T> withApnRestrictions(final int value) {
        return withTliv(ApnRestriction.ofValue(CounterType.ofValue(value)));
    }

    @Override
    public Gtp2MessageBuilder<T> withNoApnRestrictions() {
        return withApnRestrictions(0);
    }

    @Override
    public Gtp2MessageBuilder<T> withApnSelectionMode(final int value) {
        return withTliv(SelectionMode.ofValue(SelectionModeType.ofValue(value)));
    }


    @Override
    public Gtp2MessageBuilder<T> withAggregateMaximumBitRate(final int maxUplink, final int maxDownlink) {
        return withTliv(Ambr.ofValue(AmbrType.ofValue(maxUplink, maxDownlink)));
    }

    protected abstract TypeLengthInstanceValue process(int index, TypeLengthInstanceValue tliv);

    private void buildPAA() {
        if (paaIPv4Address == null && paaIPv6Address == null) {
            return;
        }

        if (paaIPv6Address != null) {
            throw new RuntimeException("Sorry, don't support PAA IPv6 addresses right now");
        }

        withTliv(Paa.ofValue(PaaType.fromIPv4(paaIPv4Address)));
    }

    private void buildBearerContext() {
        if (bearerContextToBeCreated != null) {
            withTliv(bearerContextToBeCreated.build());
        }
    }

    private void buildSenderFTeid() {
        if (senderFTeidToBeCreated != null) {
            withTliv(senderFTeidToBeCreated.build());
        }
    }

    @Override
    public final T build() {
        if (hasBeenBuilt) {
            throw new IllegalArgumentException("This GTP message has already been built once");
        }

        hasBeenBuilt = true;

        // NOTE: any additional TLIVs must be added before we create the header and calculate
        // the total length...
        buildSenderFTeid();
        buildPAA();
        buildBearerContext();

        // TODO: All of this is so dumb. I really need to create that composite buffer.
        final var header = createHeader();
        final var buffer = WritableBuffer.of(header.getTotalLength());
        buffer.write(header.getBuffer());

        for (int i = 0; i < tlivs.size(); ++i) {
            buffer.write(tlivs.get(i).getRaw());
        }

        return internalBuild(type, buffer.build(), header, Collections.unmodifiableList(tlivs));
    }

    private Gtp2Header createHeader() {
        final var header = this.header != null ? this.header.copy() : Gtp2Header.of(type);
        if (teid != null) {
            header.withTeid(teid);
        }

        header.withTlivSize(tlivSize);

        if (seqNo != null) {
            header.withSequenceNumber(seqNo);
        }
        return header.build();
    }

    protected abstract T internalBuild(final Gtp2MessageType type, final Buffer buffer,
                                       final Gtp2Header header, final List<TypeLengthInstanceValue> tlivs);

    private static class BearerContextBuilderImpl<T extends Gtp2Message> implements BearerContextBuilder<T> {

        private final Gtp2MessageBuilder<T> parent;

        private Ebi ebi;
        private BearerQualityOfServiceBuilderImpl<T> qos;

        private FTeidBuilderImpl<T, BearerContextBuilder<T>> gtpuFTeidToBeCreated;

        private BearerContextBuilderImpl(final Gtp2MessageBuilder<T> parent) {
            this.parent = parent;
        }

        @Override
        public FTeidBuilder<T, BearerContextBuilder<T>> withNewSgwFTeid() {
            assertNull(gtpuFTeidToBeCreated, "A GTP-U FTeid has already been specified");
            gtpuFTeidToBeCreated = new FTeidBuilderImpl<>(this, 2, ReferencePoint.S5, false); // false == GTP-U
            return gtpuFTeidToBeCreated;
        }

        @Override
        public BearerContextBuilder withEpsBearerId(final int value) {
            this.ebi = Ebi.ofValue(EbiType.ofValue(value));
            return this;
        }

        @Override
        public BearerQualityOfServiceBuilder<T> withNewBearerQualityOfService(final int qci) {
            assertNull(qos, "You have already specified a Bearer QoS");
            qos = new BearerQualityOfServiceBuilderImpl<>(this, qci);
            return qos;
        }

        @Override
        public Gtp2MessageBuilder<T> doneBearerContext() {
            return parent;
        }

        private BearerContext build() {
            final var bqos = qos.build();
            // TODO: need the fteid GTPU
            final var fteid = gtpuFTeidToBeCreated.build();
            final var grouped = GroupedType.ofValue(ebi, fteid, bqos);
            return BearerContext.ofValue(grouped);
        }
    }

    private static class FTeidBuilderImpl<T extends Gtp2Message, B extends Gtp2Builder<T>> implements FTeidBuilder<T, B> {

        private final B parent;
        private final FTeidType.Builder fteid;
        private final int instance;

        private FTeidBuilderImpl(final B parent, final int instance, final ReferencePoint rp, final boolean isGtpc) {
            this.parent = parent;
            this.instance = instance;
            fteid = FTeidType.create();
            fteid.withReferencePoint(rp, isGtpc);
        }

        @Override
        public FTeidBuilder<T, B> withRandomizedTeid() {
            fteid.withRandomizedTeid();
            return this;
        }

        @Override
        public FTeidBuilder<T, B> withTeid(final Teid teid) {
            fteid.withTeid(teid);
            return this;
        }

        @Override
        public FTeidBuilder<T, B> withIPv4Address(final String iPv4Address) {
            fteid.withIPv4Address(iPv4Address);
            return this;
        }

        @Override
        public B doneFTeid() {
            return parent;
        }

        private FTeid build() {
            return FTeid.ofValue(fteid.build(), instance);
        }
    }

    private static class BearerQualityOfServiceBuilderImpl<T extends Gtp2Message> implements BearerQualityOfServiceBuilder<T> {

        private final BearerContextBuilder<T> parent;
        private final QosType.Builder qos;

        private int arpPriorityLevel;
        private boolean arpPvi;
        private boolean arpPci;


        private BearerQualityOfServiceBuilderImpl(final BearerContextBuilder<T> parent, final int qci) {
            this.parent = parent;
            this.qos = QosType.ofQci(qci);
        }

        @Override
        public BearerQualityOfServiceBuilder<T> withPriorityLevel(final int level) {
            arpPriorityLevel = level;
            return this;
        }

        @Override
        public BearerQualityOfServiceBuilder<T> withPvi() {
            arpPvi = true;
            return this;
        }

        @Override
        public BearerQualityOfServiceBuilder<T> withPci() {
            arpPci = true;
            return this;
        }

        @Override
        public BearerQualityOfServiceBuilder<T> withMaximumBitRateUplink(final int value) {
            qos.withMbrUplink(value);
            return this;
        }

        @Override
        public BearerQualityOfServiceBuilder<T> withMaximumBitRateDownlink(final int value) {
            qos.withMbrDownlink(value);
            return this;
        }

        @Override
        public BearerQualityOfServiceBuilder<T> withGuaranteedBitRateUplink(final int value) {
            qos.withGbrUplink(value);
            return this;
        }

        @Override
        public BearerQualityOfServiceBuilder<T> withGuaranteedBitRateDownlink(final int value) {
            qos.withGbrDownlink(value);
            return this;
        }

        @Override
        public BearerContextBuilder<T> doneBearerQoS() {
            return parent;
        }

        private BearerQos build() {
            final var arp = ArpType.ofValue(arpPriorityLevel, arpPci, arpPvi);
            return BearerQos.ofValue(BearerQosType.ofValue(arp, qos.build()));
        }
    }
}
