package org.correos.app.addressvalidation.infrastructure.google.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleAddressResponse(
        Result result
) {}
