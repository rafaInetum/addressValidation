package org.correos.app.addressvalidation.application.mapper.util;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class RegionCodeNormalizer {

    @Named("normalizeRegionCode")
    public String normalize(String country) {
        if (country == null) return null;
        return switch (country.toUpperCase()) {
            case "ESPAÑA" -> "ES";
            case "PORTUGAL" -> "PT";
            case "ANDORRA" -> "AD";
            default -> country.substring(0, Math.min(2, country.length())).toUpperCase();
        };
    }
}
