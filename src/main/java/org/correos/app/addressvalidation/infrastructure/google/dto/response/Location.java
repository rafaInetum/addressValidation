package org.correos.app.addressvalidation.infrastructure.google.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Location(
        double latitude,
        double longitude,
        LatLng latLng // aquí modelas la variante anidada
) {}
