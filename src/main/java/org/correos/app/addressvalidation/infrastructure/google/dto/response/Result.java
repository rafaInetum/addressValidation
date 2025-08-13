package org.correos.app.addressvalidation.infrastructure.google.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Result(
        Address address,
        Verdict verdict,
        Geocode geocode
) {}
