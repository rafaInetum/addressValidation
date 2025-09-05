package org.correos.app.addressvalidation.infrastructure.google.mapper.response;

import lombok.RequiredArgsConstructor;
import org.correos.app.addressvalidation.application.model.AddressToValidate;
import org.correos.app.addressvalidation.domain.model.*;
import org.correos.app.addressvalidation.infrastructure.google.dto.response.*;
import org.correos.app.addressvalidation.infrastructure.google.mapper.response.logic.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class GoogleAddressResponseHandler {

    private final GoogleComponentReader componentReader;
    private final GoogleModalityResolver modalityResolver;
    private final CoordinatesExtractor coordinatesExtractor;
    private final ReliabilityCalculator reliabilityCalculator;
    private final NextActionDecider nextActionDecider;
    private final StringUtil stringUtil;

    /** Construye ValidatedAddress con Google. */
    public ValidatedAddress toDomain(GoogleAddressResponse response, AddressToValidate originalAddress) {
        var res = Optional.ofNullable(response).map(GoogleAddressResponse::result);
        Result result = res.orElse(null);

        // Componentes básicos
        String postalCode = componentReader.findComponentText(result, "POSTAL_CODE");

        String city = stringUtil.prefer(
                componentReader.findComponentText(result, "LOCALITY"),
                componentReader.findComponentText(result, "POSTAL_TOWN")
        );

        String province = stringUtil.prefer(
                componentReader.findComponentText(result, "ADMINISTRATIVE_AREA_LEVEL_2"),
                componentReader.findComponentText(result, "LOCALITY")
        );

        String country = stringUtil.prefer(
                componentReader.findComponentText(result, "COUNTRY"),
                Optional.ofNullable(result)
                        .map(Result::address)
                        .map(Address::postalAddress)
                        .map(PostalAddress::regionCode)
                        .orElse(null)
        );

        // Vía y número (tal cual Google)
        String streetName   = componentReader.findComponentText(result, "ROUTE");
        String streetNumber = componentReader.findComponentText(result, "STREET_NUMBER");
        String addressComplements = componentReader.findComponentText(result, "SUBPREMISE");

        // FormattedAddress (si no viene, fallback simple)
        String formattedAddress = Optional.ofNullable(result)
                .map(Result::address)
                .map(Address::formattedAddress)
                .orElse(buildFallbackFormatted(
                        streetName, streetNumber,
                        postalCode, city, province, country
                ));

        // Geocodificación / fiabilidad / siguiente acción
        GeocodeModality modality = modalityResolver.resolve(result);
        Coordinates coordinates  = coordinatesExtractor.extract(response);
        int reliability          = reliabilityCalculator.calculate(result, modality);

        String nextActionCode = Optional.ofNullable(result)
                .map(Result::verdict)
                .map(Verdict::possibleNextAction)
                .orElse(null);

        NextActionDecider.Outcome outcome = nextActionDecider.decide(
                modality, reliability, postalCode, city, nextActionCode
        );

        GeocodeInfo geocodeInfo = new GeocodeInfo(coordinates, modality, reliability, "GOOGLE");

        return new ValidatedAddress(
                originalAddress.originalAddress(),
                formattedAddress,
                city,
                postalCode,
                outcome.action(),
                outcome.message(),
                outcome.isValid(),
                List.of(),
                geocodeInfo,
                AddressStatusCode.SUCCESS,
                streetName,
                streetNumber,
                addressComplements,
                country,
                province
        );
    }

    // Fallback si Google no trae formattedAddress
    private String buildFallbackFormatted(
            String streetName, String streetNumber,
            String postalCode, String city, String province, String country
    ) {
        List<String> parts = new ArrayList<>(3);

        String line1 = joinNonBlank(" ", streetName, streetNumber);
        if (!isBlank(line1)) parts.add(line1);

        String line2 = joinNonBlank(" ", postalCode, city);
        if (!isBlank(line2)) parts.add(line2);

        String line3 = joinNonBlank(", ", province, country);
        if (!isBlank(line3)) parts.add(line3);

        return String.join(", ", parts);
    }

    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    private static String joinNonBlank(String sep, String... tokens) {
        StringBuilder sb = new StringBuilder();
        for (String t : tokens) {
            if (isBlank(t)) continue;
            if (sb.length() > 0) sb.append(sep);
            sb.append(t.trim());
        }
        return sb.toString();
    }
}
