package org.correos.app.addressvalidation.domain.addressnormalization.extractors;

import org.correos.app.addressvalidation.domain.addressnormalization.model.Complements;
import org.correos.app.addressvalidation.domain.addressnormalization.model.LocaleISO;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ComplementsExtractor {

    public Complements extract(String text, LocaleISO locale) {
        Map<String, String> extras = new HashMap<>();

        // Planta/Piso/Andar
        String planta = firstGroup(text, "\\b(PLANTA|PISO|ANDAR)\\s*([0-9A-Z]+)\\b", 2);

        // Puerta/Derecha/Izquierda/Dto
        String puerta = firstGroup(text, "\\b(PUERTA|PORTA|DTO|DCHA|DCH|DER|IZQ|ESQ|FRAC(C|Ç)AO)\\s*([0-9A-Z]+)\\b", 3);

        // Extras
        extras.put("portal", firstGroup(text, "\\b(PORTAL|BLOQUE|LOTE)\\s*([0-9A-Z]+)\\b", 2));
        extras.put("escalera", firstGroup(text, "\\b(ESCALERA|ESC)\\s*([0-9A-Z]+)\\b", 2));
        extras.put("km", firstGroup(text, "\\bKM\\s*([0-9]+([.,][0-9]+)?)\\b", 1));
        extras.put("sn", text.contains(" SN ") ? "true" : "false");

        // Observaciones (ej: "Frente a", "Junto a", etc.)
        String obs = null;
        Matcher m = Pattern.compile(
                "(FR(ENTE)?\\s+A\\s+|ENFRENTE\\s+DE\\s+|JUNTO\\s+A\\s+|CERCA\\s+DE\\s+)(.+)$"
        ).matcher(text);
        if (m.find()) {
            obs = m.group().trim();
        }

        return new Complements(planta, puerta, extras, obs);
    }

    private String firstGroup(String text, String regex, int groupIndex) {
        Matcher m = Pattern.compile(regex).matcher(text);
        return m.find() ? m.group(groupIndex).toUpperCase() : null;
    }
}
