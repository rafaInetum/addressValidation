// domain.model
package org.correos.app.addressvalidation.domain.model;

import java.util.List;

public record ValidatedAddress(
        String originalAddress,
        String formattedAddress,
        String locality,
        String postalCode,
        NextAction nextAction,
        String message,
        boolean isValid,
        List<String> suggestions,
        GeocodeInfo geocode,
        AddressStatusCode status,
        String streetName,
        String streetNumber,
        String addressComplements,
        String country,
        String province
) {
    public ValidatedAddress withSuggestions(List<String> newSuggestions) {
        return new ValidatedAddress(
                originalAddress, formattedAddress, locality, postalCode, nextAction, message, isValid,
                newSuggestions, geocode, status, streetName, streetNumber, addressComplements, country, province
        );
}

    public ValidatedAddress withGeocode(GeocodeInfo g) {
        return new ValidatedAddress(
                originalAddress, formattedAddress, locality, postalCode, nextAction, message, isValid,
                suggestions, g, status, streetName, streetNumber,addressComplements, country, province
        );
    }

    public static ValidatedAddress error(String msg) {
        return new ValidatedAddress(
                "ERROR","ERROR", "ERROR", "ERROR", null, msg, false,
                null, null, AddressStatusCode.VALIDATION_FAILED,
                null, null, null,null, null
        );
    }
}
