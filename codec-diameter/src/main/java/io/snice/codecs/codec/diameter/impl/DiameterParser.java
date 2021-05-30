package io.snice.codecs.codec.diameter.impl;

import io.snice.buffer.Buffer;
import io.snice.buffer.ReadableBuffer;
import io.snice.codecs.codec.diameter.DiameterHeader;
import io.snice.codecs.codec.diameter.DiameterMessage;
import io.snice.codecs.codec.diameter.DiameterParseException;
import io.snice.codecs.codec.diameter.avp.Avp;
import io.snice.codecs.codec.diameter.avp.AvpFramer;
import io.snice.codecs.codec.diameter.avp.AvpHeader;
import io.snice.codecs.codec.diameter.avp.FramedAvp;
import io.snice.codecs.codec.diameter.avp.api.AcctApplicationId;
import io.snice.codecs.codec.diameter.avp.api.AuthApplicationId;
import io.snice.codecs.codec.diameter.avp.api.DestinationHost;
import io.snice.codecs.codec.diameter.avp.api.DestinationRealm;
import io.snice.codecs.codec.diameter.avp.api.DisconnectCause;
import io.snice.codecs.codec.diameter.avp.api.ExperimentalResult;
import io.snice.codecs.codec.diameter.avp.api.ExperimentalResultCode;
import io.snice.codecs.codec.diameter.avp.api.HostIpAddress;
import io.snice.codecs.codec.diameter.avp.api.Msisdn;
import io.snice.codecs.codec.diameter.avp.api.OriginHost;
import io.snice.codecs.codec.diameter.avp.api.OriginRealm;
import io.snice.codecs.codec.diameter.avp.api.ProductName;
import io.snice.codecs.codec.diameter.avp.api.ResultCode;
import io.snice.codecs.codec.diameter.avp.api.SessionId;
import io.snice.codecs.codec.diameter.avp.api.SubscriberStatus;
import io.snice.codecs.codec.diameter.avp.api.SubscriptionData;
import io.snice.codecs.codec.diameter.avp.api.UserName;
import io.snice.codecs.codec.diameter.avp.api.VendorId;
import io.snice.codecs.codec.diameter.avp.api.VendorSpecificApplicationId;
import io.snice.codecs.codec.diameter.avp.impl.ImmutableAvpHeader;
import io.snice.codecs.codec.diameter.avp.impl.ImmutableFramedAvp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author jonas@jonasborjesson.com
 */
public class DiameterParser {

    public static DiameterMessage frame(final Buffer buffer) throws DiameterParseException {
        final ReadableBuffer readable = buffer.toReadableBuffer();
        final DiameterHeader header = frameHeader(readable);

        // don't need to do this. just keep going..
        final ReadableBuffer avps = readable.readBytes(header.getLength() - 20).toReadableBuffer();

        final List<FramedAvp> list = new ArrayList<>(); // TODO: what's a sensible default?

        // certain AVPs that are used in almost all messages we want to keep
        // track of since most applications will absolutely need them.
        short indexOfResultCode = -1;
        short indexOfExperimentalCode = -1;
        short indexOfOrigHost = -1;
        short indexOfOrigRealm = -1;
        short indexOfDestHost = -1;
        short indexOfDestRealm = -1;

        while (avps.getReadableBytes() > 0) {
            final int readerIndex = avps.getReaderIndex();
            FramedAvp avp = FramedAvp.frame(avps);

            if (OriginHost.CODE == avp.getCode()) {
                indexOfOrigHost = (short) list.size();
                avp = avp.ensure();
            } else if (OriginRealm.CODE == avp.getCode()) {
                indexOfOrigRealm = (short) list.size();
                avp = avp.ensure();
            } else if (DestinationRealm.CODE == avp.getCode()) {
                indexOfDestRealm = (short) list.size();
                avp = avp.ensure();
            } else if (ResultCode.CODE == avp.getCode()) {
                indexOfResultCode = (short) list.size();
                avp = avp.ensure();
            } else if (ExperimentalResult.CODE == avp.getCode()) {
                indexOfExperimentalCode = (short) list.size();
                avp = avp.ensure();
            } else if (DestinationHost.CODE == avp.getCode()) {
                indexOfDestHost = (short) list.size();
                avp = avp.ensure();
            }

            list.add(avp);

            // fail safe - if we are not making any progress
            // then we need to bail out.
            if (readerIndex == avps.getReaderIndex()) {
                throw new DiameterParseException(readerIndex, "Seems like we are stuck parsing " +
                        "AVPs for diameter message " + header.getCommandCode() + ". Bailing out");

            }
        }

        final Buffer entireMsg = buffer.slice(header.getLength());
        if (header.isRequest()) {
            return new ImmutableDiameterRequest(entireMsg, header, list, indexOfOrigHost, indexOfOrigRealm,
                    indexOfDestHost, indexOfDestRealm, indexOfResultCode, indexOfExperimentalCode);
        }
        return new ImmutableDiameterAnswer(entireMsg, header, list, indexOfOrigHost, indexOfOrigRealm,
                indexOfDestHost, indexOfDestRealm, indexOfResultCode, indexOfExperimentalCode);
    }

    /**
     * For stream based protocols, we may not get all the data at the same time and as such, we need
     * to wait for more to arrive. This method simply checks if we have enough data in the buffer to
     * fully frame the {@link DiameterMessage}.
     *
     * @param buffer
     * @return
     */
    public static boolean canFrameMessage(final Buffer buffer) {
        final ReadableBuffer readable = buffer.toReadableBuffer();
        // need 20 bytes for the header...
        if (readable.getReadableBytes() < 20) {
            return false;
        }
        final Buffer headerBuffer = readable.readBytes(20);
        final DiameterHeader header = new ImmutableDiameterHeader(headerBuffer);

        if (header.getLength() > readable.getReadableBytes() + 20) {
            return false;
        }

        return true;
    }


    public static DiameterHeader frameHeader(final ReadableBuffer buffer) throws DiameterParseException {
        if (buffer.getReadableBytes() < 20) {
            throw new DiameterParseException(0, "Cannot be a Diameter message because the header is less than 20 bytes");
        }

        final Buffer header = buffer.readBytes(20);
        return new ImmutableDiameterHeader(header);
    }

    /**
     * Convenience method for checking if this could indeed by a {@link DiameterMessage}. Use this when
     * you just want to check and not handle the {@link DiameterParseException} that would be thrown as a
     * result of this not being a diameter message.
     * <p>
     * TODO: may actually need a more specific ensure exception because right now, you don't konw if
     * it "blew" up because it is not a diameter message or because there is a "real" ensure exception.
     *
     * @param buffer
     * @return
     * @throws IOException
     */
    public static boolean couldBeDiameterMessage(final ReadableBuffer buffer) throws IOException {
        final int index = buffer.getReaderIndex();
        try {
            final DiameterHeader header = frameHeader(buffer);
            return header.validate();
        } catch (final DiameterParseException e) {
            return false;
        } finally {
            buffer.setReaderIndex(index);
        }
    }

    public static FramedAvp frameRawAvp(final ReadableBuffer buffer) throws DiameterParseException {
        final AvpHeader header = frameAvpHeader(buffer);
        // final int avpHeaderLength = header.isVendorSpecific() ? 12 : 8;
        // header.getHeaderLength();
        // final int avpHeaderLength = header.getLength();
        final Buffer data = buffer.readBytes(header.getLength() - header.getHeaderLength());
        final FramedAvp avp = new ImmutableFramedAvp(header, data);
        final int padding = avp.getPadding();
        if (padding != 0) {
            buffer.readBytes(padding);
        }

        return avp;
    }


    public static AvpHeader frameAvpHeader(final ReadableBuffer buffer) throws DiameterParseException {
        if (buffer.getReadableBytes() < 8) {
            throw new DiameterParseException("Unable to read 8 bytes from the buffer, not enough data to ensure AVP.");
        }

        // these are the flags and we need to check if the Vendor-ID bit is set and if so we need
        // another 4 bytes for the AVP Header.
        final byte flags = buffer.getByte(buffer.getReaderIndex() + 4);
        final boolean isVendorIdPresent = (flags & 0b10000000) == 0b10000000;
        final Buffer avpHeader = isVendorIdPresent ? buffer.readBytes(12) : buffer.readBytes(8);
        final Optional<Long> vendorId = isVendorIdPresent ? Optional.of(avpHeader.getUnsignedInt(8)) : Optional.empty();
        return new ImmutableAvpHeader(avpHeader, vendorId);

    }

}
