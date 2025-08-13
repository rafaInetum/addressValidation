package org.correos.app.addressvalidation.infrastructure.google.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Verdict(
        String inputGranularity,
        String validationGranularity,
        String geocodeGranularity,
        boolean isAddressComplete,
        boolean hasInferredComponents,
        String possibleNextAction
) {}
