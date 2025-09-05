package org.correos.app.addressvalidation.application.addressnormalization.extractors;

import org.correos.app.addressvalidation.application.addressnormalization.model.Complements;
import org.correos.app.addressvalidation.application.addressnormalization.model.LocaleISO;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ComplementsExtractor {

    public Complements extract(String complements, LocaleISO locale) {
        Map<String, String> extras = new HashMap<>();

        if (complements == null) {
            return new Complements(null, null, extras, null);
        }


        /* ========================
           1) Extraer portal / escalera primero
           ======================== */
        // (sin cambios de firma; llaves iguales)
        String portal   = firstGroup(complements, "\\b(PORTAL|BLOQUE|LOTE)\\b\\s+([0-9A-Z]+)\\b", 2);
        String escalera = firstGroup(complements, "\\b(ESCALERA|ESC)\\b\\s+([0-9A-Z]+)\\b", 2);

        /* ========================
           2) Planta explícita
           ======================== */
        String planta = firstGroup(complements, "\\b(PLANTA|PISO|ANDAR)\\b\\s*([0-9A-Z]+)\\b", 2);

        /* ========================
           3) Puerta explícita (ES) — sin PORTA, con espacio obligatorio
           ======================== */
        String puerta = firstGroup(
                complements,
                "\\b(PUERTA|DTO|DCHA|DCH|DER|IZQ|ESQ)\\b\\s+([0-9A-Z]+)\\b",
                2
        );

        /* ========================
           4) Fallback: token compacto 3B / 3ºB / 3-B / 3 B
           ======================== */
        if (planta == null || puerta == null) {
            Matcher t = Pattern.compile("(?i)\\b(\\d{1,2})\\s*(?:º|o|ª)?\\s*[- ]?\\s*([A-Z])(?=\\b|[,;])")
                    .matcher(complements);
            if (t.find()) {
                String pisoInf = t.group(1);
                String puertaInf = t.group(2).toUpperCase();
                if (planta == null) planta = pisoInf;
                if (puerta == null) puerta = puertaInf;
            }
        }

        /* ========================
           5) KM / S-N y observaciones (ligero ajuste a S/N)
           ======================== */
        extras.put("km", firstGroup(complements, "\\bKM\\b\\s*([0-9]+(?:[.,][0-9]+)?)\\b", 1));
        boolean sn = Pattern.compile("(?i)\\bS\\s*/?\\s*N\\b").matcher(complements).find();
        extras.put("sn", sn ? "true" : "false");

        String obs = null;
        Matcher m = Pattern.compile("(?i)(FR(?:ENTE)?\\s+A\\s+|ENFRENTE\\s+DE\\s+|JUNTO\\s+A\\s+|CERCA\\s+DE\\s+)(.+)$")
                .matcher(complements.trim());
        if (m.find()) {
            obs = m.group().trim();
        }

        /* ========================
           6) Guardar portal/escalera en complementAddress (como antes)
           ======================== */
        extras.put("portal", portal != null ? portal.toUpperCase() : null);
        extras.put("escalera", escalera != null ? escalera.toUpperCase() : null);

        /* ========================
           7) Construir addressLine de subpremisa (formateado y mayusculas)
           ======================== */
        StringBuilder sb = new StringBuilder();
        if (portal   != null) appendPart(sb, "Portal "   + portal.toUpperCase());
        if (escalera != null) appendPart(sb, "Escalera " + escalera.toUpperCase());
        if (planta   != null) appendPart(sb, ("Piso "    + planta.toUpperCase())); // si quieres "3º", cámbialo aquí
        if (puerta   != null) appendPart(sb, "Puerta "   + puerta.toUpperCase());
        String addressLine = sb.length() == 0 ? null : sb.toString().toUpperCase();
        extras.put("addressLine", addressLine); // <<-- Aquí te lo dejo construido

        return new Complements(
                planta != null ? planta.toUpperCase() : null,
                puerta != null ? puerta.toUpperCase() : null,
                extras,
                obs
        );
    }

    private void appendPart(StringBuilder sb, String part) {
        if (part == null || part.isBlank()) return;
        if (sb.length() > 0) sb.append(", ");
        sb.append(part);
    }

    private String firstGroup(String text, String regex, int groupIndex) {
        Matcher m = Pattern.compile(regex).matcher(text);
        return m.find() ? m.group(groupIndex).toUpperCase() : null;
    }
}
