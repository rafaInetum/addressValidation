package org.correos.app.addressvalidation.domain.model;

import java.util.List;

public record ValidatedAddress(
        String formattedAddress,
        String locality,
        String postalCode,
        NextAction nextAction,
        String message,
        boolean isValid,
        List<String>suggestions,
        Coordinates coordinates,
        AddressStatusCode status
) {

    public ValidatedAddress withSuggestions(List<String> newSuggestions) {
        return new ValidatedAddress(
                this.formattedAddress,
                this.locality,
                this.postalCode,
                this.nextAction,
                this.message,
                this.isValid,
                newSuggestions,
                this.coordinates,
                this.status
        );
    }

    private static final String ERROR_VALUE = "ERROR";

    public static ValidatedAddress error(String message) {
        return new ValidatedAddress(
                ERROR_VALUE, // formattedAddress
                ERROR_VALUE, // locality
                ERROR_VALUE, // postalCode
                null, // nextAction
                message, // message
                false,
                null,
                null,// geocode lat/long
                AddressStatusCode.VALIDATION_FAILED
        );
    }

}
