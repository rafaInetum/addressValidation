package org.correos.app.addressvalidation.infrastructure.google.mapper.response.logic;

import org.springframework.stereotype.Component;

@Component
public class CountryNormalizer {

    /** Normaliza país a una forma estable (misma lógica exacta). */
    public String normalizeCountry(String country) {
        if (country == null) return null;
        String u = country.trim();
        if (u.equalsIgnoreCase("ES")) return "España";
        if (u.equalsIgnoreCase("Espanya")) return "España";
        return u; // ya vendrá "España", "France", etc.
    }
}
