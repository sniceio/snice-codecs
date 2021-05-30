package io.snice.codecs.codec.diameter;

import io.snice.buffer.Buffer;
import io.snice.buffer.ReadableBuffer;
import io.snice.codecs.codec.diameter.avp.Avp;
import io.snice.codecs.codec.diameter.avp.FramedAvp;
import io.snice.codecs.codec.diameter.avp.api.DestinationHost;
import io.snice.codecs.codec.diameter.avp.api.DestinationRealm;
import io.snice.codecs.codec.diameter.avp.api.ExperimentalResultCode;
import io.snice.codecs.codec.diameter.avp.api.OriginHost;
import io.snice.codecs.codec.diameter.avp.api.OriginRealm;
import io.snice.codecs.codec.diameter.avp.api.ResultCode;
import io.snice.codecs.codec.diameter.avp.api.SessionId;
import io.snice.codecs.codec.diameter.avp.api.UserName;
import io.snice.codecs.codec.diameter.impl.DiameterParser;
import io.snice.functional.Either;
import io.snice.preconditions.PreConditions;
import io.snice.preconditions.ValidationError;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author jonas@jonasborjesson.com
 */
public interface DiameterMessage extends Cloneable {

    DiameterHeader getHeader();

    List<FramedAvp> getAllAvps();

    DiameterMessage clone();

    /**
     * Get an {@link FramedAvp}. If there are multiple AVPs present,
     * only the first one will be returned. Use {@link #getAvps(long)}
     * to retrieve those AVPs that may be present multiple times.
     *
     * @param code
     * @return an {@link Optional} {@link FramedAvp}
     */
    Optional<FramedAvp> getAvp(long code);

    /**
     * Retrieve all AVPs for the given code.
     *
     * @param code
     * @return a list of AVPs, or an empty list of none were found.
     */
    default List<FramedAvp> getAvps(final long code) {
        return getAllAvps().stream().filter(avp -> avp.getCode() == code).collect(Collectors.toList());
    }

    /**
     * By default, no validation on the {@link DiameterMessage} is made since the philosophy of the
     * library is to give users maximum flexibility, including the ability to construct illegal
     * {@link DiameterMessage} (important if you want to be able to create test tools etc).
     * However, if you are to build a proper diameter stack you may want to validate all messages
     * before giving them to user space so e.g. {@link DiameterRequest#getOriginHost()} is always
     * returning a non-null {@link OriginHost} (as per diameter specification). In that case, call
     * this method, which will validate the message according to RFC 6733.
     *
     * @return an {@link Either} where no validation errors, the {@link Either.Right} will contain this
     * {@link DiameterMessage} or if there were issues found with the message, the {@link Either.Left} will
     * contain the {@link ValidationError} describing what failed.
     */
    default Either<ValidationError<String>, ? extends DiameterMessage> validate() {
        return MessageValidators.rfc6733BaseValidation(this);
    }

    default Either<ValidationError<String>, ? extends DiameterMessage> validate(
            DiameterMessage msg,
            Function<DiameterMessage, Either<ValidationError<String>, DiameterMessage>> validator) {
        PreConditions.assertNotNull(validator, "Unable to validate the Diameter Message since the validator was null");
        return validator.apply(msg);
    }

    Builder<? extends DiameterMessage> copy();

    default boolean isRequest() {
        return false;
    }

    default DiameterRequest toRequest() {
        throw new ClassCastException("Unable to cast this " + getClass().getName()
                + " into a " + DiameterRequest.class.getName());
    }

    default boolean isAnswer() {
        return false;
    }

    default DiameterAnswer toAnswer() {
        throw new ClassCastException("Unable to cast this " + getClass().getName()
                + " into a " + DiameterAnswer.class.getName());
    }

    /**
     * Check if this is a Update-Location-Request as defined by
     * 3GPP 29.272 for the interface s6ad
     */
    default boolean isULR() {
        final var header = getHeader();
        return header.isRequest() && header.getCommandCode() == CommandCode.UpdateLocation.getCode();
    }

    /**
     * Check if this is a Update-Location-Answer as defined by
     * 3GPP 29.272 for the interface s6ad
     */
    default boolean isULA() {
        final var header = getHeader();
        return header.isAnswer() && header.getCommandCode() == CommandCode.UpdateLocation.getCode();
    }

    /**
     * Check if this is a Cancel-Location-Request as defined by
     * 3GPP 29.272 for the interface s6ad
     */
    default boolean isCLR() {
        final var header = getHeader();
        return header.isRequest() && header.getCommandCode() == CommandCode.CancelLocation.getCode();
    }

    /**
     * Check if this is a Cancel-Location-Answer as defined by
     * 3GPP 29.272 for the interface s6ad
     */
    default boolean isCLA() {
        final var header = getHeader();
        return header.isAnswer() && header.getCommandCode() == CommandCode.CancelLocation.getCode();
    }

    /**
     * Check if this is a Authentication-Information-Request as defined by
     * 3GPP 29.272 for the interface s6ad
     */
    default boolean isAIR() {
        final var header = getHeader();
        return header.isRequest() && header.getCommandCode() == CommandCode.AuthenticationInformation.getCode();
    }

    /**
     * Check if this is a Authentication-Information-Answer as defined by
     * 3GPP 29.272 for the interface s6ad
     */
    default boolean isAIA() {
        final var header = getHeader();
        return header.isAnswer() && header.getCommandCode() == CommandCode.AuthenticationInformation.getCode();
    }

    /**
     * Check if this is a Insert-Subscriber-Data-Request as defined by
     * 3GPP 29.272 for the interface s6ad
     */
    default boolean isIDR() {
        final var header = getHeader();
        return header.isRequest() && header.getCommandCode() == CommandCode.InsertSubscriberData.getCode();
    }

    /**
     * Check if this is a Insert-Subscriber-Data-Answer as defined by
     * 3GPP 29.272 for the interface s6ad
     */
    default boolean isIDA() {
        final var header = getHeader();
        return header.isAnswer() && header.getCommandCode() == CommandCode.InsertSubscriberData.getCode();
    }

    /**
     * Check if this is a Delete-Subscriber-Data-Request as defined by
     * 3GPP 29.272 for the interface s6ad
     */
    default boolean isDSR() {
        final var header = getHeader();
        return header.isRequest() && header.getCommandCode() == CommandCode.DeleteSubscriberData.getCode();
    }

    /**
     * Check if this is a Delete-Subscriber-Data-Answer as defined by
     * 3GPP 29.272 for the interface s6ad
     */
    default boolean isDSA() {
        final var header = getHeader();
        return header.isAnswer() && header.getCommandCode() == CommandCode.DeleteSubscriberData.getCode();
    }

    /**
     * Check if this is a Purge-UE-Request as defined by
     * 3GPP 29.272 for the interface s6ad
     */
    default boolean isPUR() {
        final var header = getHeader();
        return header.isRequest() && header.getCommandCode() == CommandCode.PurgeUe.getCode();
    }

    /**
     * Check if this is a Purge-UE-Answer as defined by
     * 3GPP 29.272 for the interface s6ad
     */
    default boolean isPUA() {
        final var header = getHeader();
        return header.isAnswer() && header.getCommandCode() == CommandCode.PurgeUe.getCode();
    }

    /**
     * Check if this is a Reset-Request as defined by
     * 3GPP 29.272 for the interface s6ad
     */
    default boolean isRSR() {
        final var header = getHeader();
        return header.isRequest() && header.getCommandCode() == CommandCode.Reset.getCode();
    }

    /**
     * Check if this is a Reset-Answer as defined by
     * 3GPP 29.272 for the interface s6ad
     */
    default boolean isRSA() {
        final var header = getHeader();
        return header.isAnswer() && header.getCommandCode() == CommandCode.Reset.getCode();
    }

    /**
     * Check if this is a Notify-Request as defined by
     * 3GPP 29.272 for the interface s6ad
     */
    default boolean isNOR() {
        final var header = getHeader();
        return header.isRequest() && header.getCommandCode() == CommandCode.Notify.getCode();
    }

    /**
     * Check if this is a Notify-Answer as defined by
     * 3GPP 29.272 for the interface s6ad
     */
    default boolean isNOA() {
        final var header = getHeader();
        return header.isAnswer() && header.getCommandCode() == CommandCode.Notify.getCode();
    }

    default boolean isDWR() {
        final var header = getHeader();
        return header.isRequest() && header.getCommandCode() == 280;
    }

    default boolean isDWA() {
        final var header = getHeader();
        return header.isAnswer() && header.getCommandCode() == 280;
    }

    default boolean isDPR() {
        final var header = getHeader();
        return header.isRequest() && header.getCommandCode() == 282;
    }

    default boolean isDPA() {
        final var header = getHeader();
        return header.isAnswer() && header.getCommandCode() == 282;
    }

    default boolean isCER() {
        final var header = getHeader();
        return header.isRequest() && header.getCommandCode() == 257;
    }

    default boolean isCEA() {
        final var header = getHeader();
        return header.isAnswer() && header.getCommandCode() == 257;
    }

    /**
     * Get the entire {@link DiameterMessage} as a {@link Buffer}, which you then can use
     * to e.g. write to network socket.
     */
    Buffer getBuffer();

    /**
     * The {@link OriginHost} MUST be present in all diameter messages.
     *
     * @return the {@link OriginHost} or, as discussed in {@link #getOriginRealm()}, null if it isn't present which
     * means that this is actually a malformed {@link DiameterMessage}.
     */
    OriginHost getOriginHost();

    /**
     * The {@link OriginRealm} MUST be present in all diameter messages. But is actually
     * allowed to be null, see note below.
     *
     * However, even though this is indeed a mandatory AVP the snice diameter
     * library does not enforce it by default. The reason is that if you are a
     * security researcher, or someone that is perhaps building testing tools, you
     * may want to be able to create an incorrect {@link DiameterMessage}.
     *
     * It is up to the actual diameter application/stack to, if that author so wishes, to
     * enforce this so that applications built on top of it is guaranteed this.
     *
     * @return the {@link OriginRealm} or, as discussed above, null if it isn't present which
     * means that this is actually a malformed {@link DiameterMessage}.
     */
    OriginRealm getOriginRealm();

    Optional<DestinationRealm> getDestinationRealm();

    Optional<DestinationHost> getDestinationHost();

    static DiameterMessage frame(final Buffer buffer) {
        return DiameterParser.frame(buffer.toReadableBuffer());
    }

    static DiameterMessage frame(final ReadableBuffer buffer) {
        return DiameterParser.frame(buffer);
    }

    /**
     * Create a new answer based on this {@link DiameterMessage}. If this
     * {@link DiameterMessage} is not a {@link DiameterRequest} then a
     * {@link ClassCastException} will be thrown. Only the mandatory {@link Avp}s
     * from the {@link DiameterRequest} are copied. Those mandatory AVPs are:
     * <ul>
     * <li>TODO</li>
     * </ul>
     *
     * @param resultCode
     * @return
     * @throws DiameterParseException in case anything goes wrong when parsing out AVPs from the
     *                                {@link DiameterMessage}
     */
    default DiameterAnswer.Builder createAnswer(final ResultCode resultCode) throws DiameterParseException, ClassCastException {
        throw new ClassCastException("Unable to cast this " + getClass().getName()
                + " into a " + DiameterAnswer.class.getName());
    }

    default DiameterAnswer.Builder createAnswer(final ExperimentalResultCode resultCode) throws DiameterParseException, ClassCastException {
        throw new ClassCastException("Unable to cast this " + getClass().getName()
                + " into a " + DiameterAnswer.class.getName());
    }

    interface Builder<T extends DiameterMessage> {

        default boolean isDiameterRequestBuilder() {
            return false;
        }

        default boolean isDiameterAnswerBuilder() {
            return false;
        }

        default DiameterMessage.Builder<DiameterRequest> toDiameterRequestBuilder() {
            throw new ClassCastException("Cannot cast " + getClass().getName() + " into a "
                    + DiameterRequest.class.getName() + " builder");
        }

        default DiameterMessage.Builder<DiameterAnswer> toDiameterResponseBuilder() {
            throw new ClassCastException("Cannot cast " + getClass().getName() + " into a "
                    + DiameterAnswer.class.getName() + " builder");
        }

        /**
         * Make a deep copy of this builder. You have to do this if you wish to use this
         * builder as a "template" since once you've built it once, you cannot build it again.
         *
         */
        Builder<T> copy();

        /**
         * Whenever an {@link Avp} is about to be pushed onto the new {@link DiameterMessage}
         * you have a chance to change the value of that AVP. You do so
         * by registering a function that accepts an {@link Avp} as an argument and that
         * returns a {@link Avp}, which is the AVP that will be pushed onto the new
         * {@link DiameterMessage}. If you do not want to include the AVP, then simply return
         * null and that AVP will be dropped.
         * <p>
         * If you wish to leave the AVP un-touched, then simply return it has is.
         *
         * @param f
         * @return
         * @throws IllegalStateException in case a function already had been registered with
         *                               this builder.
         */
        Builder<T> onAvp(Function<Avp, Avp> f) throws IllegalStateException;

        /**
         * Add the {@link Avp} to the list of AVPs already specified within this builder.
         * The {@link Avp} will be added last to the list of AVPs.
         *
         * @param avp
         * @return
         */
        Builder<T> withAvp(Avp avp);

        Builder<T> withOriginHost(String originHost);
        Builder<T> withOriginHost(Buffer originHost);
        Builder<T> withOriginHost(OriginHost originHost);

        Builder<T> withOriginRealm(Buffer originHost);
        Builder<T> withOriginRealm(String originHost);
        Builder<T> withOriginRealm(OriginRealm originHost);

        Builder<T> withDestinationHost(DestinationHost destHost);
        Builder<T> withDestinationHost(Buffer destHost);
        Builder<T> withDestinationHost(String destHost);

        Builder<T> withDestinationRealm(Buffer destRealm);
        Builder<T> withDestinationRealm(String destRealm);
        Builder<T> withDestinationRealm(DestinationRealm destRealm);

        Builder<T> withUserName(Buffer userName);
        Builder<T> withUserName(String userName);
        Builder<T> withUserName(UserName userName);

        Builder<T> withSessionId(Buffer sessionId);
        Builder<T> withSessionId(String sessionId);
        Builder<T> withSessionId(SessionId sessionId);

        /**
         * The length of the entire message must be encoded into the {@link DiameterHeader}
         * and as such, we need access to it within the {@link Builder} and we'll calculate
         * the correct length upon {@link #build()}.
         *
         * @param header
         */
        Builder<T> withDiameterHeader(final DiameterHeader.Builder header);

        T build();

        /**
         * After the {@link DiameterMessage} has been fully built and created, the "end result"
         * will be conveyed to the registered function. It is utterly important
         * that the function returns as quickly as possible since the build method
         * will not be able to return until the call to this function has been completed.
         *
         * @param f
         */
        Builder<T> onCommit(Consumer<DiameterMessage> f);

    }
}
