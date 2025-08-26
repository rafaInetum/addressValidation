package org.correos.app.addressvalidation.application.addressnormalization.service;

import org.correos.app.addressvalidation.application.addressnormalization.extractors.CityExtractor;
import org.correos.app.addressvalidation.application.addressnormalization.extractors.ComplementsExtractor;
import org.correos.app.addressvalidation.application.addressnormalization.model.*;
import org.correos.app.addressvalidation.application.addressnormalization.extractors.StreetExtractor;
import org.correos.app.addressvalidation.application.addressnormalization.preprocessor.AddressPreprocessor;
import org.correos.app.addressvalidation.application.addressnormalization.rule.NumberNormalizer;
import org.correos.app.addressvalidation.application.addressnormalization.scorer.AddressScorer;
import org.correos.app.addressvalidation.domain.model.*;
import org.correos.app.addressvalidation.application.addressnormalization.utils.AddressUtils;
import org.springframework.stereotype.Service;


@Service
public class AddressNormalizerServiceImpl implements AddressNormalizer {

    private final AddressPreprocessor preprocessor;
    private final ComplementsExtractor complementsExtractor;
    private final StreetExtractor streetExtractor;
    private final CityExtractor cityExtractor;
    private final AddressScorer addressScorer;
    private final NumberNormalizer numberNormalizer;

    public AddressNormalizerServiceImpl(
            AddressPreprocessor preprocessor,
            ComplementsExtractor complementsExtractor,
            StreetExtractor streetExtractor,
            CityExtractor cityExtractor,
            AddressScorer addressScorer,
            NumberNormalizer numberNormalizer
    ) {
        this.preprocessor = preprocessor;
        this.complementsExtractor = complementsExtractor;
        this.streetExtractor = streetExtractor;
        this.cityExtractor = cityExtractor;
        this.addressScorer = addressScorer;
        this.numberNormalizer = numberNormalizer;
    }

    @Override
    public NormalizedAddress normalize(RawAddress input) {
        String raw = input.rawText() == null ? "" : input.rawText().trim();
        if (raw.isEmpty()) return empty("ES");

        PreprocessedAddress pre = preprocessor.preprocess(raw, input.localeHint());

        String expanded = pre.normalizedText();
        LocaleISO locale = pre.locale();
        Lexicon lexicon = pre.lexicon();

        String cp = AddressUtils.matchFirst(lexicon.cpPattern(), expanded);
        String country = AddressUtils.detectCountry(expanded, locale);

        Complements complements = complementsExtractor.extract(expanded, locale);
        StreetParts street = streetExtractor.extract(expanded, lexicon);

        String numeroVia = numberNormalizer.normalize(street.numero(), locale);
        String planta = numberNormalizer.normalizeFloor(complements.planta(), locale);

        String puerta = numberNormalizer.normalizeDoor(complements.puerta());
        CityParts city = cityExtractor.extract(expanded, cp, country);
        double confidence = addressScorer.calculateScore(street, cp, city, country, complements);

        return new NormalizedAddress(
                street.tipo(),
                street.nombre(),
                numeroVia,
                planta,
                puerta,
                cp,
                city.localidad(),
                city.provincia(),
                country,
                complements.observaciones(),
                complements.extras(),
                confidence,
                locale.name()
        );
    }

    private NormalizedAddress empty(String locale) {
        return new NormalizedAddress(
                null, null, null, null, null, null,
                null, null, null, null, java.util.Map.of(), 0.0, locale
        );
    }
}
