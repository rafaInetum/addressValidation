package org.correos.app.addressvalidation.infrastructure.google.mapper;

import org.correos.app.addressvalidation.domain.model.*;
import org.correos.app.addressvalidation.infrastructure.google.dto.response.*;
import org.correos.app.addressvalidation.infrastructure.google.mapper.logic.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class GoogleAddressMapper {

    private final GoogleComponentReader compReader;
    private final GoogleFormattedAddressBuilder formattedBuilder;
    private final CountryNormalizer countryNormalizer;
    private final GoogleModalityResolver modalityResolver;
    private final CoordinatesExtractor coordinatesExtractor;
    private final ReliabilityCalculator reliabilityCalculator;
    private final NextActionDecider nextActionDecider;
    private final StringUtil stringUtil;

    public GoogleAddressMapper(GoogleComponentReader compReader,
                               GoogleFormattedAddressBuilder formattedBuilder,
                               CountryNormalizer countryNormalizer,
                               GoogleModalityResolver modalityResolver,
                               CoordinatesExtractor coordinatesExtractor,
                               ReliabilityCalculator reliabilityCalculator,
                               NextActionDecider nextActionDecider,
                               StringUtil stringUtil) {
        this.compReader = compReader;
        this.formattedBuilder = formattedBuilder;
        this.countryNormalizer = countryNormalizer;
        this.modalityResolver = modalityResolver;
        this.coordinatesExtractor = coordinatesExtractor;
        this.reliabilityCalculator = reliabilityCalculator;
        this.nextActionDecider = nextActionDecider;
        this.stringUtil = stringUtil;
    }

    /**
     * Combina NormalizedAddress con la respuesta de Google.
     * - Tipo/Nombre/Número: SIEMPRE desde lo normalizado (n).
     * - CP/Localidad/País/coords/veredicto: desde Google con fallback a n.
     */
    public ValidatedAddress toDomain(GoogleAddressResponse response, NormalizedAddress n) {
        var res = Optional.ofNullable(response).map(GoogleAddressResponse::result);
        Result result = res.orElse(null);

        // ---- Componentes oficiales de Google (con fallback a postalAddress y a lo normalizado) ----
        String postalFromG   = compReader.findComponentText(result, "POSTAL_CODE");
        String localityFromG = stringUtil.prefer(
                compReader.findComponentText(result, "LOCALITY"),
                compReader.findComponentText(result, "POSTAL_TOWN")
        );

        String countryFromG  = stringUtil.prefer(
                compReader.findComponentText(result, "COUNTRY"),
                Optional.ofNullable(result)
                        .map(Result::address)
                        .map(Address::postalAddress)
                        .map(PostalAddress::regionCode)
                        .orElse(null)
        );

        String cpFinal   = stringUtil.prefer(postalFromG,   n.codigoPostal());
        String locFinal  = stringUtil.prefer(localityFromG, n.localidad());
        String paisFinal = countryNormalizer.normalizeCountry(stringUtil.prefer(countryFromG, n.pais()));

        // formatted preferible de Google; si falta, se construye con lo normalizado
        String formatted = Optional.ofNullable(result)
                .map(Result::address)
                .map(Address::formattedAddress)
                .orElse(formattedBuilder.buildFormatted(n));

        // ---- Granularidad → modalidad ----
        GeocodeModality modality = modalityResolver.resolve(result);

        // Coordenadas
        Coordinates coords = coordinatesExtractor.extract(response);

        // Fiabilidad (incluyendo penalización por unconfirmed)
        int reliability = reliabilityCalculator.calculate(result, modality);

        String nextActionCode = Optional.ofNullable(result)
                .map(Result::verdict)
                .map(Verdict::possibleNextAction)
                .orElse(null);

        // Regla de aceptación: portal claro + CP + localidad (misma que tenías)
        NextActionDecider.Outcome outcome = nextActionDecider.decide(
                modality, reliability, cpFinal, locFinal, nextActionCode
        );

        GeocodeInfo geocode = new GeocodeInfo(coords, modality, reliability, "GOOGLE");

        // ---- Devolver ValidatedAddress (misma construcción/orden de campos) ----
        return new ValidatedAddress(
                formatted,
                locFinal,          // locality
                cpFinal,           // postalCode
                outcome.action(),
                outcome.message(),
                outcome.isValid(),
                List.of(),         // si generas sugerencias, rellénalas en el adaptador
                geocode,
                AddressStatusCode.SUCCESS,
                n.tipoVia(),       // streetType (normalizador)
                n.nombreVia(),     // streetName (normalizador)
                n.numero(),        // streetNumber (normalizador)
                paisFinal          // country (Google → fallback normalizado → normalizado a "España/ES")
        );
    }
}
