package org.correos.app.addressvalidation.domain.addressnormalization.extractors;

import org.correos.app.addressvalidation.domain.addressnormalization.model.Lexicon;
import org.correos.app.addressvalidation.domain.addressnormalization.model.StreetParts;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class StreetExtractor {

    public StreetParts extract(String text, Lexicon lexicon) {
        String tipos = String.join("|", lexicon.tipos());

        // Caso 1: tipo al principio ("CALLE MAYOR 5")
        Pattern p1 = Pattern.compile("\\b(" + tipos + ")\\b\\s+((?:[A-ZÀ-ÿ\\s]+?))\\s+(\\d+[A-Z]?)\\b");
        Matcher m1 = p1.matcher(text);
        if (m1.find()) {
            return new StreetParts(m1.group(1), m1.group(2).trim(), m1.group(3));
        }

        // Caso 2: tipo al final ("FERMIN CALBETON KALEA, 5")
        Pattern p2 = Pattern.compile("((?:[A-ZÀ-ÿ\\s]+?))\\s+(" + tipos + ")\\b\\s*,?\\s*(\\d+[A-Z]?)\\b");
        Matcher m2 = p2.matcher(text);
        if (m2.find()) {
            return new StreetParts(m2.group(2), m2.group(1).trim(), m2.group(3));
        }

        // Caso 3: Fallback "Nombre, número"
        Pattern fallback = Pattern.compile("([^,]+?)\\s*,\\s*(\\d+[A-Z]?)\\b");
        Matcher m3 = fallback.matcher(text);
        if (m3.find()) {
            return new StreetParts(null, m3.group(1).trim(), m3.group(2));
        }

        // Caso 4: Solo tipo y nombre sin número (ej: "CALLE JOSE")
        Pattern soloNombre = Pattern.compile("\\b(" + tipos + ")\\b\\s+([A-ZÀ-ÿ\\s]+?)\\b");
        Matcher m4 = soloNombre.matcher(text);
        if (m4.find()) {
            return new StreetParts(m4.group(1), m4.group(2).trim(), null);
        }

        return new StreetParts(null, null, null);
    }
}
