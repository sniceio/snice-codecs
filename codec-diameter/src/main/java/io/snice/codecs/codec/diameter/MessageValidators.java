package io.snice.codecs.codec.diameter;

import io.snice.codecs.codec.diameter.avp.api.DestinationHost;
import io.snice.codecs.codec.diameter.avp.api.DestinationRealm;
import io.snice.functional.Either;
import io.snice.preconditions.PreConditions;
import io.snice.preconditions.ValidationError;

import static io.snice.preconditions.PreConditions.assertNotNull;

/**
 * Simple class with only functions to validate a {@link DiameterMessage} and is being
 * used by {@link DiameterMessage#validate()}.
 *
 */
public final class MessageValidators {

    /**
     * The most basic of all validations and follows RFC6733 section 6.1.1 for requests and 6.2 for
     * Diameter Answer.
     *
     * @param msg
     * @return
     */
    public static Either<ValidationError<String>, ? extends DiameterMessage> rfc6733BaseValidation(final DiameterMessage msg) {
        assertNotNull(msg, "Cannot validate a null Diameter Message");

        ValidationError<String> validation = null;

        if (msg.getOriginHost() == null) {
            validation = ValidationError.append(validation, "OriginHost is missing");
        }

        if (msg.getOriginRealm() == null) {
            validation = ValidationError.append(validation, "OriginRealm is missing");
        }

        if (msg.isAnswer()) {
            validation = rfc6733BaseValidationForAnswer(validation, msg.toAnswer());
        }

        if (validation != null) {
            return Either.left(validation);
        }

        return Either.right(msg);
    }

    /**
     * Helper method to check the rules for a {@link DiameterAnswer} and follows RFC6733 Section 6.2
     * which here really just checks the most basic conditions, such as to ensure that the {@link DestinationHost}
     * and {@link DestinationRealm} isn't present on any of the messages.
     */
    private static ValidationError<String> rfc6733BaseValidationForAnswer(final ValidationError<String> v, final DiameterAnswer answer) {
        ValidationError<String> validation = v;
        if (answer.getDestinationHost().isPresent()) {
            validation = ValidationError.append(validation, "Destination Host must not be present on an Answer");
        }

        if (answer.getDestinationRealm().isPresent()) {
            validation = ValidationError.append(validation, "Destination Realm must not be present on an Answer");
        }

        return validation;
    }
}
