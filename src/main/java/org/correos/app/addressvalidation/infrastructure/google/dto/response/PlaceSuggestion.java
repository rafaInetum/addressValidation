package org.correos.app.addressvalidation.infrastructure.google.dto.response;

public record PlaceSuggestion(
        String name,
        String formattedAddress,
        String postalCode
) {}