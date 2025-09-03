package org.correos.app.addressvalidation.api.dto.response;

public record SuggestionResponseDTO(
        String placeId,
        String mainText,
        String secondaryText
) {}
