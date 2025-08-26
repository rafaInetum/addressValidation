package org.correos.app.addressvalidation.application.addressnormalization.extractors;

import org.correos.app.addressvalidation.application.addressnormalization.model.Lexicon;
import org.correos.app.addressvalidation.application.addressnormalization.model.StreetParts;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class StreetExtractor {

    // Letras/números Unicode + espacios + apóstrofo + punto + guion
    private static final String NAME_CHARS = "[\\p{L}\\p{N}\\s'\\.-]";

    public StreetParts extract(String text, Lexicon lexicon) {
        if (text == null || text.isBlank()) {
            return new StreetParts(null, null, null);
        }

        // Construye la alternancia de tipos escapando y priorizando los más largos (p.ej. "GRAN VIA")
        String tipos = lexicon.tipos().stream()
                .sorted((a, b) -> Integer.compare(b.length(), a.length()))
                .map(Pattern::quote)
                .collect(Collectors.joining("|"));

        // 1) Tipo al principio:  TIPO  NOMBRE  ,?  NUM
        Pattern p1 = Pattern.compile(
                "\\b(" + tipos + ")\\b\\s+(" + NAME_CHARS + "+?)\\s*,?\\s*(\\d+[\\p{L}\\p{N}]?)\\b",
                Pattern.UNICODE_CASE | Pattern.UNICODE_CHARACTER_CLASS
        );
        Matcher m1 = p1.matcher(text);
        if (m1.find()) {
            return new StreetParts(m1.group(1), trimName(m1.group(2)), m1.group(3));
        }

        // 2) Tipo al final:  NOMBRE  TIPO  ,?  NUM
        Pattern p2 = Pattern.compile(
                "(" + NAME_CHARS + "+?)\\s+(" + tipos + ")\\b\\s*,?\\s*(\\d+[\\p{L}\\p{N}]?)\\b",
                Pattern.UNICODE_CASE | Pattern.UNICODE_CHARACTER_CLASS
        );
        Matcher m2 = p2.matcher(text);
        if (m2.find()) {
            return new StreetParts(m2.group(2), trimName(m2.group(1)), m2.group(3));
        }

        // 3) Solo tipo + nombre (sin número): "CALLE SANTA CRUZ DE MUDELA"
        Pattern p3 = Pattern.compile(
                "\\b(" + tipos + ")\\b\\s+(" + NAME_CHARS + "+?)(?:\\s*,|$)",
                Pattern.UNICODE_CASE | Pattern.UNICODE_CHARACTER_CLASS
        );
        Matcher m3 = p3.matcher(text);
        if (m3.find()) {
            return new StreetParts(m3.group(1), trimName(m3.group(2)), null);
        }

        // 4) Fallback "Nombre, número"
        Pattern p4 = Pattern.compile(
                "(" + NAME_CHARS + "+?)\\s*,\\s*(\\d+[\\p{L}\\p{N}]?)\\b",
                Pattern.UNICODE_CASE | Pattern.UNICODE_CHARACTER_CLASS
        );
        Matcher m4 = p4.matcher(text);
        if (m4.find()) {
            return new StreetParts(null, trimName(m4.group(1)), m4.group(2));
        }

        return new StreetParts(null, null, null);
    }

    private static String trimName(String s) {
        if (s == null) return null;
        String t = s.replaceAll("\\s{2,}", " ").trim();
        t = t.replaceAll("^[,\\s]+|[,\\s]+$", "");
        return t.isBlank() ? null : t;
    }
}
