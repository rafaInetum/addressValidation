package org.correos.app.addressvalidation.domain.model;

import java.util.Map;

public record NormalizedAddress(
        String streetType,
        String streetName,
        String streetNumber,
        String floor,
        String door,
        String postalCode,
        String locality,
        String province,
        String country,
        String notes,
        Map<String,String> extras,
        double confidence,
        String locale
) {}
