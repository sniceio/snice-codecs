package io.snice.codecs.codec.gtp.gtpc.v2.Impl;

import io.snice.buffer.Buffer;
import io.snice.codecs.codec.MccMnc;
import io.snice.codecs.codec.gtp.GtpParseException;
import io.snice.codecs.codec.gtp.Teid;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Builder;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Header;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Message;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.BearerContextBuilder;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.FTeid;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.FTeidBuilder;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.Imsi;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.TypeLengthInstanceValue;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.UeTimeZone;
import io.snice.codecs.codec.gtp.gtpc.v2.type.PdnType;
import io.snice.codecs.codec.gtp.gtpc.v2.type.RatType;

/**
 * Generic interface for all various types of {@link Gtp2Message} builders.
 * <p>
 * Builders are used when you want to create a new {@link Gtp2Message} from scratch,
 * or to modify an existing one. This is unlike the {@link Gtp2MessageFramer}, which
 * assumes you have an entire message already in a single {@link Buffer} and you just
 * want to frame it out (the use case being you've typically read it from the network)
 *
 * @param <T>
 */
public interface Gtp2MessageBuilder<T extends Gtp2Message> extends Gtp2Builder<T> {

    Gtp2MessageBuilder<T> withHeader(Gtp2Header header);

    /**
     * Specify the {@link Teid} of the message (part of the {@link Gtp2Header}
     */
    Gtp2MessageBuilder<T> withTeid(Teid teid);

    Gtp2MessageBuilder<T> withTeid(Buffer teid);

    /**
     * Specify the sequence number of the message (part of the {@link Gtp2Header})
     */
    Gtp2MessageBuilder<T> withSeqNo(Buffer teid);

    /**
     * Have the builder generate a random sequence number, which will be
     * inserted into the {@link Gtp2Header}
     */
    Gtp2MessageBuilder<T> withRandomSeqNo();

    /**
     * Generic method for adding any type of {@link TypeLengthInstanceValue}.
     *
     * @param tliv the {@link TypeLengthInstanceValue} to add to this message.
     */
    Gtp2MessageBuilder<T> withTliv(TypeLengthInstanceValue tliv);

    /**
     * TS 29.274 section 8.3 - International Mobile Subscriber Identity (IMSI)
     */
    Gtp2MessageBuilder<T> withImsi(String imsi);

    /**
     * TS 29.274 section 8.3 - International Mobile Subscriber Identity (IMSI)
     */
    Gtp2MessageBuilder<T> withImsi(Imsi imsi);

    /**
     * TS 29.274 section 8.6 - Access Point Name (APN)
     */
    Gtp2MessageBuilder<T> withApn(String apn);

    /**
     * TS 29.274 section 8.7 - Aggregate Maximum Bit Rate (AMBR)
     *
     * @param maxUplink
     * @param maxDownlink
     */
    Gtp2MessageBuilder<T> withAggregateMaximumBitRate(int maxUplink, int maxDownlink);

    /**
     * TS 29.274 section 8.10 - Mobile Equipment Identity (MEI)
     * <p>
     * The MEI represents a unique identifier of the UE and is BCD encoded.
     *
     * @param imei the human readable IMEI, which will be BCD encoded (hence, don't
     *             BCD encode the supplied IMEI yourself)
     */
    Gtp2MessageBuilder<T> withMobileEquipmentIdentity(String imei);

    /**
     * TS 29.274 section 8.11 - MSISDN
     *
     * @param msisdn the MSISDN in a human readable format, which will be
     *               encoded into TBCD automatically.
     */
    Gtp2MessageBuilder<T> withMsisdn(String msisdn);

    /**
     * TS 29.274 section 8.14 - PDN Address Allocation (PAA)
     * <p>
     * Specify the IPv4 address for the PAA.
     * <p>
     * Calling this method AND {@link #withIPv6PdnAddressAllocation(String)} will
     * result in a PAA containing both an IPv4 and an IPv6 address and subsequently,
     * the {@link PdnType} will be set to {@link PdnType.Type#IPv4v6}
     */
    Gtp2MessageBuilder<T> withIPv4PdnAddressAllocation(String ipv4Address);

    /**
     * TS 29.274 section 8.14 - PDN Address Allocation (PAA)
     * <p>
     * Specify the IPv6 address for the PAA.
     * <p>
     * Calling this method AND {@link #withIPv4PdnAddressAllocation(String)} will
     * result in a PAA containing both an IPv4 and an IPv6 address and subsequently,
     * the {@link PdnType} will be set to {@link PdnType.Type#IPv4v6}
     */
    Gtp2MessageBuilder<T> withIPv6PdnAddressAllocation(String ipv6Address);

    /**
     * TS 29.274 section 8.17 - Radio Access Technology
     */
    Gtp2MessageBuilder<T> withRat(int rat);

    /**
     * TS 29.274 section 8.17 - Radio Access Technology
     */
    Gtp2MessageBuilder<T> withRat(RatType rat);

    /**
     * TS 29.274 section 8.18 - Serving Network.
     * <p>
     * This is a convenience method for calling {@link MccMnc#parseAsMccMnc(String)}
     *
     * @param mccMnc the MCC and MNC of the serving network.
     */
    Gtp2MessageBuilder<T> withServingNetwork(String mccMnc);

    /**
     * TS 29.274 section 8.22 - Fully Qualified TEID (F-Teid).
     *
     * <p>
     * This is a convenience method for constructing a new {@link FTeid}
     * that is set as a GTP-C sender FTeid (instance 0)
     * <p>
     * TODO: should perhaps be moved to tunnel management messages only?
     *
     * @return
     */
    FTeidBuilder<T, Gtp2MessageBuilder<T>> withNewSenderControlPlaneFTeid();

    /**
     * TS 29.274 section 8.28 - Bearer Context
     */
    BearerContextBuilder<T> withNewBearerContext();

    Gtp2MessageBuilder<T> withUeTimeZone(UeTimeZone tz);

    /**
     * TS 29.274 section 8.34 - PDN Type
     */
    Gtp2MessageBuilder<T> withPdnType(PdnType.Type type);

    /**
     * TS 29.274 section 8.57 - APN Restriction.
     * <p>
     * <i>The APN Restriction information element contains an unsigned integer value indicating the level of restriction imposed
     * on EPS Bearer Contexts created to the associated APN.</i>
     * </p>
     * <p>
     * Valid values are:
     * <ul>
     *     <li>0: No Existing Contexts or Restriction</li>
     *     <li>1: Public-1</li>
     *     <li>2: Public-2</li>
     *     <li>3: Private-1</li>
     *     <li>4: Private-2</li>
     * </ul>
     * <p>
     * However, since this library is also meant to be used to test other services with, no enforcement is done
     * by default to ensure only these values are used.
     */
    Gtp2MessageBuilder<T> withApnRestrictions(int value);

    /**
     * TS 29.274 section 8.57 - APN Restriction.
     * <p>
     * Convenience method for calling {@link #withApnRestrictions(int)} with a value of 0 (zero)
     */
    Gtp2MessageBuilder<T> withNoApnRestrictions();

    /**
     * TS 29.274 section 8.58 - Selection Mode
     * <p>
     * The Selection mode information element indicates the origin of the APN in the message.
     * <p>
     * The only valid values are:
     * <ul>
     *     <li>Value 0 (decimal): MS or network provided APN, subscription verified</li>
     *     <li>Value 1 (decimal): MS provided APN, subscription not verified</li>
     *     <li>Value 2 (decimal): Network provided APN, subscription not verified</li>
     *     <li>Value 3 (decimal): For future use. Shall not be sent. If received, shall be interpreted as the value "2".</li>
     * </ul>
     * <p>
     * Note that even though the specification says that you should not send 3, this library does not
     * enforce it by default. The reason being is that it should be possible to build test tools using
     * this Snice GTP Codev library and as such, it allows you to send "bad" values.
     */
    Gtp2MessageBuilder<T> withApnSelectionMode(int value);

    /**
     * Create a new {@link FTeid} through its builder.
     *
     * @return
     */
    // FTeidType.Builder createFTeid();

    /**
     * Build the {@link Gtp2Message}
     *
     * @throws IllegalArgumentException in case this message has already been built.
     * @throws GtpParseException        in case anything has not been constructed properly.
     */
    T build() throws IllegalArgumentException, GtpParseException;
}
