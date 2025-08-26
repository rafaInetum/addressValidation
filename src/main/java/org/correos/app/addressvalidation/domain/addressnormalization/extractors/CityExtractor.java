package org.correos.app.addressvalidation.domain.addressnormalization.extractors;

import org.correos.app.addressvalidation.domain.addressnormalization.model.CityParts;
import org.springframework.stereotype.Component;

@Component
public class CityExtractor {

    public CityParts extract(String text, String cp, String pais) {
        if (cp != null) {
            // Tomamos lo que viene después del CP
            String tail = text.substring(text.indexOf(cp) + cp.length()).replace(",", " ").trim();
            String[] parts = tail.split("\\s+");
            String loc = parts.length > 0 ? parts[0] : null;
            return new CityParts(loc, null);
        }

        if (pais != null && text.toUpperCase().contains(pais)) {
            // Tomamos lo que viene antes del país
            String head = text.toUpperCase().substring(0, text.toUpperCase().indexOf(pais)).trim();
            String[] tokens = head.split(",\\s*");
            String loc = tokens.length > 0 ? tokens[tokens.length - 1].trim() : null;
            return new CityParts(loc, null);
        }

        return new CityParts(null, null);
    }
}
