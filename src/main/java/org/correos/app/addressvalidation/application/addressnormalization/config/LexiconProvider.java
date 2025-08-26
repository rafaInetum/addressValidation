package org.correos.app.addressvalidation.application.addressnormalization.config;

import org.correos.app.addressvalidation.application.addressnormalization.model.Lexicon;
import org.correos.app.addressvalidation.application.addressnormalization.model.LocaleISO;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@Component
public class LexiconProvider {

    public Lexicon getLexiconFor(LocaleISO locale) {
        return switch (locale) {
            case PT -> lexPT();
            case AD -> lexAD();
            default -> lexES();
        };
    }

    public Lexicon lexES() {
        return new Lexicon(
                Map.ofEntries(
                        // Castellano
                        Map.entry("C/", "CALLE"), Map.entry("CL", "CALLE"), Map.entry("C", "CALLE"),
                        Map.entry("AVDA", "AVENIDA"), Map.entry("AV", "AVENIDA"), Map.entry("AV.", "AVENIDA"),
                        Map.entry("PZA", "PLAZA"), Map.entry("PLZ", "PLAZA"), Map.entry("PL", "PLAZA"), Map.entry("P.", "PISO"),
                        Map.entry("CR", "CARRETERA"), Map.entry("CRA", "CARRETERA"), Map.entry("CTRA", "CARRETERA"),
                        Map.entry("PSO", "PASEO"), Map.entry("PG", "PASEO"), Map.entry("TRV", "TRAVESIA"), Map.entry("TRVA", "TRAVESIA"),
                        // Euskera
                        Map.entry("K", "KALEA"), Map.entry("KAL", "KALEA"), Map.entry("KAL.", "KALEA"),
                        Map.entry("ET", "ETORBIDEA"), Map.entry("ETORB", "ETORBIDEA"), Map.entry("ETORB.", "ETORBIDEA"),
                        Map.entry("BID", "BIDEA"), Map.entry("BID.", "BIDEA"),
                        Map.entry("ENP", "ENPARANTZA"), Map.entry("ENP.", "ENPARANTZA"),
                        Map.entry("PSK", "PASEALEKUA"), Map.entry("PSK.", "PASEALEKUA"),
                        Map.entry("ERREP", "ERREPIDEA"), Map.entry("ERREP.", "ERREPIDEA"),
                        Map.entry("HIR", "HIRIBIDEA"), Map.entry("HIR.", "HIRIBIDEA"),
                        Map.entry("ZK", "ZEHARKALEA"), Map.entry("ZK.", "ZEHARKALEA"),
                        Map.entry("BBG", "BIRIBILGUNEA"), Map.entry("BBG.", "BIRIBILGUNEA"),

                        // Catalán
                        Map.entry("CARRER", "CALLE"),
                        Map.entry("CARR", "CALLE"),        // por si viene "CARR." (tú le quitas el punto)
                        Map.entry("AVINGUDA", "AVENIDA"),
                        Map.entry("PLACA", "PLAZA"),       // "PLAÇA" → "PLACA" tras tu preprocesado
                        Map.entry("PASSEIG", "PASEO"),
                        Map.entry("TRAVESSA", "TRAVESIA"),
                        Map.entry("TRAVESSERA", "TRAVESIA")
                ),
                Set.of(
                        "CALLE", "AVENIDA", "PLAZA", "CARRETERA", "PASEO", "TRAVESIA",
                        "CAMINO", "RONDA", "GLORIETA", "BULEVAR",
                        "KALEA", "KARRIKA", "ETORBIDEA", "BIDEA", "ENPARANTZA",
                        "PASEALEKUA", "ERREPIDEA", "HIRIBIDEA", "ZEHARKALEA", "BIRIBILGUNEA"
                ),
                Pattern.compile("\\b\\d{5}\\b")
        );
    }

    public Lexicon lexPT() {
        return new Lexicon(
                Map.ofEntries(
                        Map.entry("R", "RUA"), Map.entry("R.", "RUA"),
                        Map.entry("AV", "AVENIDA"), Map.entry("AV.", "AVENIDA"),
                        Map.entry("PC", "PRAÇA"), Map.entry("PC.", "PRAÇA"),
                        Map.entry("LG", "LARGO"), Map.entry("LG.", "LARGO"),
                        Map.entry("TV", "TRAVESSA"), Map.entry("TV.", "TRAVESSA")
                ),
                Set.of("RUA", "AVENIDA", "PRAÇA", "LARGO", "TRAVESSA"),
                Pattern.compile("\\b\\d{4}-\\d{3}\\b")
        );
    }

    public Lexicon lexAD() {
        return lexES();
    }
}
