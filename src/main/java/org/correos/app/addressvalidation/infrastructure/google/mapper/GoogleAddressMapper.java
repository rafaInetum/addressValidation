package org.correos.app.addressvalidation.infrastructure.google.mapper;

import org.correos.app.addressvalidation.application.addressnormalization.extractors.StreetExtractor;
import org.correos.app.addressvalidation.application.addressnormalization.model.LocaleISO;
import org.correos.app.addressvalidation.application.addressnormalization.model.PreprocessedAddress;
import org.correos.app.addressvalidation.application.addressnormalization.model.StreetParts;
import org.correos.app.addressvalidation.application.addressnormalization.preprocessor.AddressPreprocessor;
import org.correos.app.addressvalidation.domain.model.*;
import org.correos.app.addressvalidation.infrastructure.google.dto.response.*;
import org.correos.app.addressvalidation.infrastructure.google.mapper.logic.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class GoogleAddressMapper {

    private final GoogleComponentReader componentReader;
    private final GoogleFormattedAddressBuilder formattedAddressBuilder;
    private final CountryNormalizer countryNormalizer;
    private final GoogleModalityResolver modalityResolver;
    private final CoordinatesExtractor coordinatesExtractor;
    private final ReliabilityCalculator reliabilityCalculator;
    private final NextActionDecider nextActionDecider;
    private final StringUtil stringUtil;
    private final StreetExtractor streetExtractor;
    private final AddressPreprocessor preprocessor;

    public GoogleAddressMapper(GoogleComponentReader componentReader,
                               GoogleFormattedAddressBuilder formattedAddressBuilder,
                               CountryNormalizer countryNormalizer,
                               GoogleModalityResolver modalityResolver,
                               CoordinatesExtractor coordinatesExtractor,
                               ReliabilityCalculator reliabilityCalculator,
                               NextActionDecider nextActionDecider,
                               StringUtil stringUtil,
                               StreetExtractor streetExtractor,
                               AddressPreprocessor preprocessor
    ) {
        this.componentReader = componentReader;
        this.formattedAddressBuilder = formattedAddressBuilder;
        this.countryNormalizer = countryNormalizer;
        this.modalityResolver = modalityResolver;
        this.coordinatesExtractor = coordinatesExtractor;
        this.reliabilityCalculator = reliabilityCalculator;
        this.nextActionDecider = nextActionDecider;
        this.stringUtil = stringUtil;
        this.streetExtractor = streetExtractor;
        this.preprocessor = preprocessor;
    }

    /**
     * Combines NormalizedAddress with Google's response.
     * - Street type/name/number: uses normalizedAddress if present; falls back to Google components otherwise.
     * - Postal code / City / Province / Country / Coordinates / Verdict: from Google with fallback to normalized input.
     */
    public ValidatedAddress toDomain(GoogleAddressResponse response, NormalizedAddress normalizedAddress) {
        var res = Optional.ofNullable(response).map(GoogleAddressResponse::result);
        Result result = res.orElse(null);

        // Google components (fallback to NormalizedAddress if needed)
        String postalCodeFromGoogle = componentReader.findComponentText(result, "POSTAL_CODE");
        String localityFromGoogle = stringUtil.prefer(
                componentReader.findComponentText(result, "LOCALITY"),
                componentReader.findComponentText(result, "POSTAL_TOWN")
        );

        String provinceFromGoogle = stringUtil.prefer(
                componentReader.findComponentText(result, "ADMINISTRATIVE_AREA_LEVEL_2"),
                componentReader.findComponentText(result, "LOCALITY")
        );

        String countryFromGoogle = stringUtil.prefer(
                componentReader.findComponentText(result, "COUNTRY"),
                Optional.ofNullable(result)
                        .map(Result::address)
                        .map(Address::postalAddress)
                        .map(PostalAddress::regionCode)
                        .orElse(null)
        );

        String finalPostalCode = stringUtil.prefer(postalCodeFromGoogle, normalizedAddress.postalCode());
        String finalCity = stringUtil.prefer(localityFromGoogle, normalizedAddress.city());
        String finalProvince = stringUtil.prefer(provinceFromGoogle, normalizedAddress.province());
        String finalCountry = countryNormalizer.normalizeCountry(stringUtil.prefer(countryFromGoogle, normalizedAddress.country()));

        // Fallback for street type/name/number
        String streetFromGoogle = componentReader.findComponentText(result, "ROUTE");
        String numberFromGoogle = componentReader.findComponentText(result, "STREET_NUMBER");

        String finalStreetType = normalizedAddress.streetType();              // Google no separa type; lo inferimos si falta
        String finalStreetName = stringUtil.prefer(normalizedAddress.streetName(), streetFromGoogle);
        String finalStreetNumber = stringUtil.prefer(normalizedAddress.streetNumber(), numberFromGoogle);

        if ((finalStreetType == null || finalStreetType.isBlank()) ||
                (finalStreetName == null || finalStreetName.isBlank())) {

            // 1) Mapear país -> LocaleISO (igual que harías en el normalizador)
            LocaleISO locale =
                    "PT".equalsIgnoreCase(finalCountry) || "PORTUGAL".equalsIgnoreCase(finalCountry) ? LocaleISO.PT :
                            "AD".equalsIgnoreCase(finalCountry) || "ANDORRA".equalsIgnoreCase(finalCountry) ? LocaleISO.AD :
                                    LocaleISO.ES;

            // 2) Preprocesar el ROUTE de Google con tu AddressPreprocessor
            String route = streetFromGoogle != null ? streetFromGoogle : "";
            PreprocessedAddress pre = preprocessor.preprocess(route, locale.name());

            // 3) Extraer tipo/nombre con el mismo StreetExtractor y el lexicon del preprocesado
            StreetParts parts = streetExtractor.extract(pre.normalizedText(), pre.lexicon());

            if (finalStreetType == null || finalStreetType.isBlank()) {
                finalStreetType = parts.type();
            }
            if (finalStreetName == null || finalStreetName.isBlank()) {
                finalStreetName = parts.name();
            }
        }

        String formattedAddress = Optional.ofNullable(result)
                .map(Result::address)
                .map(Address::formattedAddress)
                .orElse(formattedAddressBuilder.buildFormatted(normalizedAddress));

        GeocodeModality modality = modalityResolver.resolve(result);
        Coordinates coordinates = coordinatesExtractor.extract(response);
        int reliability = reliabilityCalculator.calculate(result, modality);

        String nextActionCode = Optional.ofNullable(result)
                .map(Result::verdict)
                .map(Verdict::possibleNextAction)
                .orElse(null);

        NextActionDecider.Outcome outcome = nextActionDecider.decide(
                modality, reliability, finalPostalCode, finalCity, nextActionCode
        );

        GeocodeInfo geocodeInfo = new GeocodeInfo(coordinates, modality, reliability, "GOOGLE");

        return new ValidatedAddress(
                formattedAddress,
                finalCity,
                finalPostalCode,
                outcome.action(),
                outcome.message(),
                outcome.isValid(),
                List.of(),
                geocodeInfo,
                AddressStatusCode.SUCCESS,
                finalStreetType,
                finalStreetName,
                finalStreetNumber,
                finalCountry,
                finalProvince
        );
    }
}
