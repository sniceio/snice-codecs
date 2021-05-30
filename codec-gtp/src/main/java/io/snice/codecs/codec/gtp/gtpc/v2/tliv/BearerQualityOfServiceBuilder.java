package io.snice.codecs.codec.gtp.gtpc.v2.tliv;

import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Message;

/**
 * TS 29.274 section 8.15 - Bearer Quality of Service (Bearer QoS)
 *
 * <p>
 * Specialized builder to aid in constructing a {@link BearerQos} and is meant to be used in conjunction
 * with {@link BearerContextBuilder} and as such, there is actually no builder method on it (the {@link BearerContextBuilder}
 * will be the one building it and adding it as a {@link TypeLengthInstanceValue} to the grouped {@link BearerContext}).
 */
public interface BearerQualityOfServiceBuilder<T extends Gtp2Message> {

    /**
     * @param level the priority level, which must be between 0-15 (inclusive).
     */
    BearerQualityOfServiceBuilder<T> withPriorityLevel(int level);

    /**
     * Set the Pre-emption Vulnerability (PVI) flag to true/on. By default, this flag is set to false/off.
     * <p>
     * See 3GPP TS 29.212 section 5.3.47 Pre-emption-Vulnerability AVP.
     */
    BearerQualityOfServiceBuilder<T> withPvi();

    /**
     * Set the Pre-emption Vulnerability (PVI) flag to true/on. By default, this flag is set to false/off.
     * <p>
     * See 3GPP TS 29.212 section 5.3.47 Pre-emption-Vulnerability AVP.
     */
    BearerQualityOfServiceBuilder<T> withPci();

    /**
     * <p>
     * Specify the Maximum Bit Rate for Uplink in kilobits per second.
     *
     * <p>
     * <b>NOTE: 1 kps = 1000 bps</b> and as specification says:
     *
     * <p>
     * <i>
     * The UL/DL MBR and GBR fields are encoded as kilobits per second (1 kbps = 1000 bps) in binary value. The UL/DL
     * MBR and GBR fields may require converting values in bits per second to kilobits per second when the UL/DL MBR
     * and GBR values are received from an interface other than GTPv2 interface. If such conversions result in fractions, then
     * the value of UL/DL MBR and GBR fields shall be rounded upwards. For non-GBR bearers, both the UL/DL MBR and
     * GBR should be set to zero. The range of QCI, Maximum bit rate for uplink, Maximum bit rate for downlink,
     * Guaranteed bit rate for uplink and Guaranteed bit rate for downlink are specified in 3GPP TS 36.413 [10].
     * </i>
     * </p>
     *
     * @param value the rate expressed in kilobits/second
     */
    BearerQualityOfServiceBuilder<T> withMaximumBitRateUplink(int value);

    /**
     * <p>
     * Specify the Maximum Bit Rate for Downlink in kilobits per second.
     *
     * <p>
     * <b>NOTE: 1 kps = 1000 bps</b> and as specification says:
     *
     * <p>
     * <i>
     * The UL/DL MBR and GBR fields are encoded as kilobits per second (1 kbps = 1000 bps) in binary value. The UL/DL
     * MBR and GBR fields may require converting values in bits per second to kilobits per second when the UL/DL MBR
     * and GBR values are received from an interface other than GTPv2 interface. If such conversions result in fractions, then
     * the value of UL/DL MBR and GBR fields shall be rounded upwards. For non-GBR bearers, both the UL/DL MBR and
     * GBR should be set to zero. The range of QCI, Maximum bit rate for uplink, Maximum bit rate for downlink,
     * Guaranteed bit rate for uplink and Guaranteed bit rate for downlink are specified in 3GPP TS 36.413 [10].
     * </i>
     * </p>
     *
     * @param value the rate expressed in kilobits/second
     */
    BearerQualityOfServiceBuilder<T> withMaximumBitRateDownlink(int value);

    /**
     * <p>
     * Specify the Guaranteed Bit Rate for Uplink in kilobits per second.
     *
     * <p>
     * <b>NOTE: 1 kps = 1000 bps</b> and as specification says:
     *
     * <p>
     * <i>
     * The UL/DL MBR and GBR fields are encoded as kilobits per second (1 kbps = 1000 bps) in binary value. The UL/DL
     * MBR and GBR fields may require converting values in bits per second to kilobits per second when the UL/DL MBR
     * and GBR values are received from an interface other than GTPv2 interface. If such conversions result in fractions, then
     * the value of UL/DL MBR and GBR fields shall be rounded upwards. For non-GBR bearers, both the UL/DL MBR and
     * GBR should be set to zero. The range of QCI, Maximum bit rate for uplink, Maximum bit rate for downlink,
     * Guaranteed bit rate for uplink and Guaranteed bit rate for downlink are specified in 3GPP TS 36.413 [10].
     * </i>
     * </p>
     *
     * @param value the rate expressed in kilobits/second
     */
    BearerQualityOfServiceBuilder<T> withGuaranteedBitRateUplink(int value);

    /**
     * <p>
     * Specify the Guaranteed Bit Rate for Downlink in kilobits per second.
     *
     * <p>
     * <b>NOTE: 1 kps = 1000 bps</b> and as specification says:
     *
     * <p>
     * <i>
     * The UL/DL MBR and GBR fields are encoded as kilobits per second (1 kbps = 1000 bps) in binary value. The UL/DL
     * MBR and GBR fields may require converting values in bits per second to kilobits per second when the UL/DL MBR
     * and GBR values are received from an interface other than GTPv2 interface. If such conversions result in fractions, then
     * the value of UL/DL MBR and GBR fields shall be rounded upwards. For non-GBR bearers, both the UL/DL MBR and
     * GBR should be set to zero. The range of QCI, Maximum bit rate for uplink, Maximum bit rate for downlink,
     * Guaranteed bit rate for uplink and Guaranteed bit rate for downlink are specified in 3GPP TS 36.413 [10].
     * </i>
     * </p>
     *
     * @param value the rate expressed in kilobits/second
     */
    BearerQualityOfServiceBuilder<T> withGuaranteedBitRateDownlink(int value);

    /**
     * Optionally call this method when you are done constructing the {@link BearerQos} and want to "get back"
     * to the {@link BearerContextBuilder} to continue building the {@link BearerContext}. This is just a
     * convenience method to get a fluid builder API.
     */
    BearerContextBuilder<T> doneBearerQoS();
}
