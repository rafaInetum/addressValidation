package org.correos.app.addressvalidation.application.addressnormalization.model;

public record CityParts(
        String country,
        String locality,
        String province
) {}
