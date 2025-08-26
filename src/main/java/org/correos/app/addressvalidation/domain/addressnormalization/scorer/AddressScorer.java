package org.correos.app.addressvalidation.domain.addressnormalization.scorer;

import org.correos.app.addressvalidation.domain.addressnormalization.model.CityParts;
import org.correos.app.addressvalidation.domain.addressnormalization.model.Complements;
import org.correos.app.addressvalidation.domain.addressnormalization.model.StreetParts;
import org.springframework.stereotype.Component;

@Component
public class AddressScorer {

    public double calculateScore(StreetParts street, String postalCode, CityParts city, String country, Complements complements) {
        double score = 0.0;

        if (street.nombre() != null && !street.nombre().isBlank()) {
            score += 0.3;
        }

        boolean hasNumber = street.numero() != null && !street.numero().isBlank();
        boolean hasSN = complements.extras().getOrDefault("sn", "false").equalsIgnoreCase("true");
        if (hasNumber || hasSN) {
            score += 0.25;
        }

        if (postalCode != null && !postalCode.isBlank()) {
            score += 0.2;
        }

        if (city.localidad() != null && !city.localidad().isBlank()) {
            score += 0.15;
        }

        if (country != null && !country.isBlank()) {
            score += 0.1;
        }

        return Math.min(1.0, score);
    }
}
