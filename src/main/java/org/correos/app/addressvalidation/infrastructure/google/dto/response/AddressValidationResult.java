package org.correos.app.addressvalidation.infrastructure.google.dto.response;

import java.util.List;

public record AddressValidationResult(
        boolean confirmedAddressByGoogle,
        boolean needsReview,
        String message,
        String formattedAddress,
        List<PlaceSuggestion> suggestions
) {}
