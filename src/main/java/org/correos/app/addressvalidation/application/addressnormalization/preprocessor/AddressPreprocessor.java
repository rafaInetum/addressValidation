package org.correos.app.addressvalidation.application.addressnormalization.preprocessor;

import org.correos.app.addressvalidation.application.addressnormalization.config.LexiconProvider;
import org.correos.app.addressvalidation.application.addressnormalization.model.Lexicon;
import org.correos.app.addressvalidation.application.addressnormalization.model.PreprocessedAddress;
import org.correos.app.addressvalidation.application.addressnormalization.model.LocaleISO;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AddressPreprocessor {

    private final LexiconProvider lexiconProvider;

    public AddressPreprocessor(LexiconProvider lexiconProvider) {
        this.lexiconProvider = lexiconProvider;
    }

    public PreprocessedAddress preprocess(String rawText, String localeHint) {
        String raw = Optional.ofNullable(rawText).orElse("").trim();
        if (raw.isEmpty()) {
            return new PreprocessedAddress("", LocaleISO.ES, lexiconProvider.getLexiconFor(LocaleISO.ES));
        }

        String up = clean(raw);
        LocaleISO locale = detectLocale(up, localeHint);
        Lexicon lexicon = lexiconProvider.getLexiconFor(locale);
        String expanded = expandAbbreviations(up, lexicon);

        return new PreprocessedAddress(expanded, locale, lexicon);
    }

    private String clean(String s) {
        return s
                .replaceAll("[\\t\\n]+", " ")
                .replaceAll("\\s{2,}", " ")
                .replace("º", "")
                .replace("°", "")
                .replaceAll("\\s*,\\s*", ", ")
                .trim();
    }

    private LocaleISO detectLocale(String s, String hint) {
        if (hint != null) {
            try {
                return LocaleISO.valueOf(hint.toUpperCase());
            } catch (Exception ignored) {}
        }
        String u = s.toUpperCase();
        if (u.matches(".*\\b\\d{4}-\\d{3}\\b.*") || u.contains("PORTUGAL")) return LocaleISO.PT;
        if (u.contains("ANDORRA") || u.matches(".*\\bAD\\d{3}\\b.*")) return LocaleISO.AD;
        return LocaleISO.ES;
    }

    private String expandAbbreviations(String s, Lexicon lex) {
        String u = s.toUpperCase()
                .replace(".", "")
                .replace("/", " ")
                .replace("º", "")
                .replace("°", "")
                .replaceAll("\\s{2,}", " ")
                .replace("Á", "A").replace("É", "E").replace("Í", "I")
                .replace("Ó", "O").replace("Ú", "U").replace("Ç", "C")
                .replaceAll("\\bSTA\\b", "SANTA")
                .replaceAll("\\bST\\b", "SAN")
                .replaceAll("S/N", " SN ");

        for (var e : lex.abbr().entrySet()) {
            u = u.replaceAll("\\b" + e.getKey() + "\\b", e.getValue());
        }

        return u.replaceAll("\\s{2,}", " ").trim();
    }
}
