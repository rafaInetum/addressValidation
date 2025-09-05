package org.correos.app.addressvalidation.domain.model;

import java.util.List;
import java.util.Map;

public record NormalizedAddress(
        String originalAddress,
        String formattedAdress,
        String streetType,
        String streetName,
        String streetNumber,
        String floor,
        String door,
        String postalCode,
        String locality,
        String province,
        String country,
        String observaciones,
        Map<String, String> complementAddress,
        double confidence,
        String locale,
        List<String> addressLines
) {
    private static final Map<String, String> EMPTY_EXTRAS = Map.of();
    private static final List<String> EMPTY_LINES = List.of();

    public static NormalizedAddress empty(String locale) {
        return new NormalizedAddress(
                null,null,null, null, null, null, null, null,
                null, null, null, null, EMPTY_EXTRAS,
                0.0, locale, EMPTY_LINES
        );
    }
}
