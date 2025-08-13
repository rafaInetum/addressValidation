package org.correos.app.addressvalidation.infrastructure.google.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PostalAddress(
        String regionCode,
        String languageCode,
        String postalCode,
        String administrativeArea,
        String locality,
        List<String> addressLines
) {}
