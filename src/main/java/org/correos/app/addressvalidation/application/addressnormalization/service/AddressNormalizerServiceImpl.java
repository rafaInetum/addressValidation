package org.correos.app.addressvalidation.application.addressnormalization.service;

import lombok.RequiredArgsConstructor;
//import org.correos.app.addressvalidation.application.addressnormalization.extractors.CityExtractor;
import org.correos.app.addressvalidation.application.addressnormalization.extractors.ComplementsExtractor;
import org.correos.app.addressvalidation.application.addressnormalization.extractors.StreetExtractor;
import org.correos.app.addressvalidation.application.addressnormalization.model.*;
import org.correos.app.addressvalidation.application.addressnormalization.preprocessor.AddressPreNormalizer;
import org.correos.app.addressvalidation.application.addressnormalization.rule.NumberNormalizer;
import org.correos.app.addressvalidation.application.addressnormalization.scorer.AddressScorer;
import org.correos.app.addressvalidation.domain.model.NormalizedAddress;
import org.correos.app.addressvalidation.domain.model.ValidatedAddress;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@RequiredArgsConstructor
@Service
public class AddressNormalizerServiceImpl implements AddressNormalizer {

    private final AddressPreNormalizer preprocessor;
    private final ComplementsExtractor complementsExtractor;
    private final StreetExtractor streetExtractor;
    private final AddressScorer addressScorer;
    private final NumberNormalizer numberNormalizer;

    /* =========================
        Main methods
        ========================= */
    @Override
    public NormalizedAddress normalize(ValidatedAddress validated) {

        // === Guard 0) Entrada nula o vacía
        if (validated == null || validated.formattedAddress() == null || validated.formattedAddress().trim().isEmpty()) {
            return empty("ES");
        }

        // === 0) Preproceso / normalización básica
        PreNormalizedAddress pre = preprocessor.preProcess(validated);

        String expanded = pre.normalizedText();
        LocaleISO locale = pre.locale();
        Lexicon lexicon = pre.lexicon();

        // === 1) Señales básicas (CP, país, provincia, localidad
        String cp = pre.postalCode();
        String country = pre.country();
        String province = pre.province();
        String locality = pre.locality();
        CityParts cityParts = new CityParts(cp, locality, province);

        // === 2) Extractores principales (complementos y calle)
        Complements complements = complementsExtractor.extract(pre.addressComplements(), locale);

        // === 3) Extraer tipo/nombre/número de vía
        String plainStreetParts = Stream.of(pre.streetName(), pre.streetNumber())
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(" "));

        StreetParts street = streetExtractor.extract(plainStreetParts, lexicon);

        // === 4) Normalizadores de número / planta / puerta
        String numeroVia = numberNormalizer.normalize(street.number(), locale);
        String floor = numberNormalizer.normalizeFloor(complements.planta(), locale);
        String door = numberNormalizer.normalizeDoor(complements.puerta());

        // === 5) Scoring
        double confidence = addressScorer.calculateScore(street, cp, cityParts, country, complements);

        List<String> addressLines =  List.of(expanded);

        return new NormalizedAddress(
                validated.formattedAddress(),
                validated.originalAddress(),
                street.type(),
                street.name(),
                numeroVia,
                floor,
                door,
                cp,
                locality,
                province,
                country,
                complements.observaciones(),
                complements.complementAddress(),
                confidence,
                locale.name(),
                addressLines
        );
    }

    /* =========================
        Helpers
        ========================= */
    private NormalizedAddress empty(String locale) {
        return NormalizedAddress.empty("ES");
    }
}
