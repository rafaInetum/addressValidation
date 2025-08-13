package org.correos.app.addressvalidation.domain.model;

import org.correos.app.addressvalidation.infrastructure.google.dto.response.Geocode;

import java.util.List;

public record ValidatedAddress(
        String formattedAddress,
        String locality,
        String postalCode,
        String nextAction,
        String message,
        boolean isValid,
        List<String>suggestions,
        Coordinates coordinates,
        AddressErrorCode error
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
                this.error
        );
    }

    public static ValidatedAddress error(String message) {
        return new ValidatedAddress(
                "ERROR", // formattedAddress
                "ERROR", // locality
                "ERROR", // postalCode
                "ERROR", // nextAction
                message, // message
                false,
                null,
                null,// geocode lat/long
                AddressErrorCode.VALIDATION_FAILED
        );
    }

}
