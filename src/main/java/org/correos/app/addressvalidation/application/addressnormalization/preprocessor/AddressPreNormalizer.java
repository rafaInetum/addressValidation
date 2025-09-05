package org.correos.app.addressvalidation.application.addressnormalization.preprocessor;

import lombok.RequiredArgsConstructor;
import org.correos.app.addressvalidation.application.addressnormalization.config.LexiconProvider;
import org.correos.app.addressvalidation.application.addressnormalization.model.*;
import org.correos.app.addressvalidation.domain.model.ValidatedAddress;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class AddressPreNormalizer {

    private final LexiconProvider lexiconProvider;


    public PreNormalizedAddress preProcess(ValidatedAddress validated) {


        String raw = Optional.ofNullable(validated.formattedAddress()).orElse("").trim();
        LocaleISO locale = resolveLocale(validated.country());
        Lexicon lexicon  = lexiconProvider.getLexiconFor(locale);

        if (raw.isEmpty()) {
            return PreNormalizedAddress.empty(locale, lexicon);
        }

        // 1) Locale por país (fallback ES)
        Locale jLoc = toJavaLocale(locale);

        // 2) Normalizaciones básicas (UPPER + trim + colapsar espacios)
        String formattedU = norm(validated.formattedAddress(), jLoc);
        String streetNameU = norm(validated.streetName(), jLoc);
        String streetNumberU = norm(validated.streetNumber(), jLoc);
        String complementsU = norm(validated.addressComplements(), jLoc);
        String cpU = normDigits(validated.postalCode());          // CP a dígitos (ej. “28.001” -> “28001”)
        String localityU = norm(validated.locality(), jLoc);
        String provinceU = norm(validated.province(), jLoc);
        String countryU = norm(validated.country(), jLoc);


        return new PreNormalizedAddress(
                formattedU,
                locale,
                lexicon,
                formattedU,
                streetNameU,
                streetNumberU,
                complementsU,
                cpU,
                localityU,
                provinceU,
                countryU
        );
    }

    /* ===== Helpers ===== */
    private static String norm(String s, Locale loc) {
        if (s == null) return null;
        String t = s.trim().replaceAll("\\s+", " ");
        // quita diacríticos y pasa a mayúsculas con el locale
        String noAccents = Normalizer.normalize(t, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return noAccents.toUpperCase(loc == null ? Locale.ROOT : loc);
    }

    private static String normDigits(String s) {
        if (s == null) return null;
        String digits = s.replaceAll("\\D+", ""); // solo dígitos
        return digits.isEmpty() ? null : digits;
    }

    private static LocaleISO resolveLocale(String country) {
        if (country == null) return LocaleISO.ES;
        String c = country.trim().toUpperCase(Locale.ROOT);
        return switch (c) {
            case "PT" -> LocaleISO.PT;
            case "AD" -> LocaleISO.AD;
            default -> LocaleISO.ES;
        };
    }

    private static Locale toJavaLocale(LocaleISO iso) {
        return switch (iso) {
            case ES -> new Locale("es", "ES");
            case PT -> new Locale("pt", "PT");
            case AD -> new Locale("ca", "AD");
        };
    }

}
