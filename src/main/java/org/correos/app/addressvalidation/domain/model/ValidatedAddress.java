// Ubicación: domain.model
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
                this.geocode,
                this.status
        );
    }

    public ValidatedAddress withGeocode(GeocodeInfo geocode) {
        return new ValidatedAddress(
                this.formattedAddress,
                this.locality,
                this.postalCode,
                this.nextAction,
                this.message,
                this.isValid,
                this.suggestions,
                geocode,
                this.status
        );
    }

    private static final String ERROR_VALUE = "ERROR";

    public static ValidatedAddress error(String message) {
        return new ValidatedAddress(
                ERROR_VALUE,
                ERROR_VALUE,
                ERROR_VALUE,
                null,
                message,
                false,
                null,
                null,
                AddressStatusCode.VALIDATION_FAILED
        );
    }
}
