package org.correos.app.addressvalidation.infrastructure.api.dto.response;

public record SuggestionResponseDTO(
        String placeId,
        String mainText,
        String secondaryText
) {}
