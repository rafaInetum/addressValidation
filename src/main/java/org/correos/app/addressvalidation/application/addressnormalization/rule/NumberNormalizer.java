// File: domain/service/helper/NumberNormalizer.java
package org.correos.app.addressvalidation.application.addressnormalization.rule;

import org.correos.app.addressvalidation.application.addressnormalization.model.LocaleISO;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NumberNormalizer {

    private static final Map<String, Integer> ES_NUM = Map.ofEntries(
            Map.entry("CERO", 0), Map.entry("UNO", 1), Map.entry("UNA", 1), Map.entry("DOS", 2),
            Map.entry("TRES", 3), Map.entry("CUATRO", 4), Map.entry("CINCO", 5), Map.entry("SEIS", 6),
            Map.entry("SIETE", 7), Map.entry("OCHO", 8), Map.entry("NUEVE", 9), Map.entry("DIEZ", 10),
            Map.entry("ONCE", 11), Map.entry("DOCE", 12), Map.entry("TRECE", 13), Map.entry("CATORCE", 14),
            Map.entry("QUINCE", 15), Map.entry("VEINTE", 20), Map.entry("TREINTA", 30),
            Map.entry("CUARENTA", 40), Map.entry("CINCUENTA", 50), Map.entry("SESENTA", 60),
            Map.entry("SETENTA", 70), Map.entry("OCHENTA", 80), Map.entry("NOVENTA", 90)
    );

    private static final Map<String, Integer> PT_NUM = Map.ofEntries(
            Map.entry("ZERO", 0), Map.entry("UM", 1), Map.entry("UMA", 1), Map.entry("DOIS", 2),
            Map.entry("DUAS", 2), Map.entry("TRES", 3), Map.entry("TRÊS", 3), Map.entry("QUATRO", 4),
            Map.entry("CINCO", 5), Map.entry("SEIS", 6), Map.entry("SETE", 7), Map.entry("OITO", 8),
            Map.entry("NOVE", 9), Map.entry("DEZ", 10), Map.entry("ONZE", 11), Map.entry("DOZE", 12),
            Map.entry("TREZE", 13), Map.entry("CATORZE", 14), Map.entry("QUINZE", 15), Map.entry("VINTE", 20),
            Map.entry("TRINTA", 30), Map.entry("QUARENTA", 40), Map.entry("CINQUENTA", 50),
            Map.entry("SESSENTA", 60), Map.entry("SETENTA", 70), Map.entry("OITENTA", 80),
            Map.entry("NOVENTA", 90)
    );

    public String normalize(String token, LocaleISO locale) {
        if (token == null) return null;
        String u = clean(token);

        Map<String, Integer> base = (locale == LocaleISO.PT) ? PT_NUM : ES_NUM;

        if (u.contains(" E ")) {
            int sum = 0;
            for (String part : u.split("\\s+E\\s+")) {
                Integer v = base.get(part);
                if (v == null) return token;
                sum += v;
            }
            return Integer.toString(sum);
        }

        if (u.startsWith("VEINTI")) {
            Integer ones = base.get(u.substring(6));
            if (ones != null) return Integer.toString(20 + ones);
        }

        Integer v = base.get(u);
        return v != null ? Integer.toString(v) : token;
    }

    public String normalizeFloor(String planta, LocaleISO locale) {
        if (planta == null) return null;
        String p = planta.trim().toUpperCase();
        if (p.equals("BAJO") || p.equals("BJO") || p.equals("R/C") || p.contains("RES")) return "0";
        return normalize(p, locale);
    }

    public String normalizeDoor(String puerta) {
        if (puerta == null) return null;
        return puerta.toUpperCase().replaceAll("\\s+", "");
    }

    private String clean(String s) {
        return s.trim().toUpperCase()
                .replace("Ê", "E").replace("É", "E")
                .replace("Ã", "A").replace("À", "A");
    }
}
