// domain.model
package org.correos.app.addressvalidation.domain.model;

import java.util.List;

public record ValidatedAddress(
        String formattedAddress,
        String locality,
        String postalCode,
        NextAction nextAction,
        String message,
        boolean isValid,
        List<String> suggestions,
        GeocodeInfo geocode,
        AddressStatusCode status,
        String streetType,
        String streetName,
        String streetNumber,
        String country
) {
    public ValidatedAddress withSuggestions(List<String> newSuggestions) {
        return new ValidatedAddress(
                formattedAddress, locality, postalCode, nextAction, message, isValid,
                newSuggestions, geocode, status, streetType, streetName, streetNumber, country
        );
    }
    public ValidatedAddress withGeocode(GeocodeInfo g) {
        return new ValidatedAddress(
                formattedAddress, locality, postalCode, nextAction, message, isValid,
                suggestions, g, status, streetType, streetName, streetNumber, country
        );
    }
    private static final String ERROR_VALUE = "ERROR";
    public static ValidatedAddress error(String msg) {
        return new ValidatedAddress(
                ERROR_VALUE, ERROR_VALUE, ERROR_VALUE, null, msg, false,
                null, null, AddressStatusCode.VALIDATION_FAILED,
                null, null, null, null
        );
    }
}
