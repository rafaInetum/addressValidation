package org.correos.app.addressvalidation.infrastructure.google.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AddressComponent(
        ComponentName componentName,
        String componentType,
        String confirmationLevel,
        boolean inferred
) {}
