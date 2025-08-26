package org.correos.app.addressvalidation.infrastructure.google.mapper;

import org.correos.app.addressvalidation.domain.model.*;
import org.correos.app.addressvalidation.infrastructure.google.dto.response.*;
import org.correos.app.addressvalidation.infrastructure.google.util.GoogleNextActionMessageResolver;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class GoogleAddressMapper {

    public ValidatedAddress toDomain(GoogleAddressResponse response) {

        var res = opt(response).map(GoogleAddressResponse::result);

        var formatted = res
                .map(Result::address)
                .map(Address::formattedAddress)
                .orElse(null);

        String locality = res
                .map(Result::address)
                .map(Address::postalAddress)
                .map(PostalAddress::locality)
                .orElse(null);

        String postal = res
                .map(Result::address)
                .map(Address::postalAddress)
                .map(PostalAddress::postalCode)
                .orElse(null);

        String nextActionCode = res
                .map(Result::verdict)
                .map(Verdict::possibleNextAction)
                .orElse(null);

        String granularity = res
                .map(Result::verdict)
                .map(Verdict::validationGranularity)
                .orElse(null);

        Coordinates coords = extractCoordinates(response);

        // 1) Modalidad
        GeocodeModality modality = switch (granularity) {
            case "PREMISE", "SUB_PREMISE" -> GeocodeModality.PORTAL;
            case "ROUTE" -> GeocodeModality.APROX_PORTAL;
            default -> GeocodeModality.CALLE;
        };

        // 2) Fiabilidad (%)
        int reliability = switch (modality) {
            case PORTAL -> {
                double meters = res.map(Result::geocode)
                        .map(Geocode::featureSizeMeters)
                        .orElse(50.0);
                yield meters < 30.0 ? 95 : 85;
            }
            case APROX_PORTAL -> 85;
            case CALLE -> 70;
            case MANUAL_FIX -> 100;  // No se usará aquí en teoría, pero lo exige el switch
        };

        // 3) Ajuste si hay componentes no confirmados
        boolean unconfirmed = res.map(Result::verdict)
                .map(Verdict::hasUnconfirmedComponents)
                .orElse(false);
        if (unconfirmed) reliability = Math.max(0, reliability - 10);

        var action  = NextAction.fromString(nextActionCode);
        var message = GoogleNextActionMessageResolver.resolve(action);

        // isValid = solo si modalidad es PORTAL y fiabilidad ≥ 85
        boolean isValid = modality == GeocodeModality.PORTAL && reliability >= 85;

        // Nuevo objeto centralizado
        GeocodeInfo geocode = new GeocodeInfo(coords, modality, reliability, "GOOGLE");

        return new ValidatedAddress(
                formatted,
                locality,
                postal,
                action,
                message,
                isValid,
                List.of(),
                geocode,
                AddressStatusCode.SUCCESS
        );
    }

    private Coordinates extractCoordinates(GoogleAddressResponse response) {
        // Variante 1: location con lat/lng directos
        var direct = opt(response)
                .map(GoogleAddressResponse::result)
                .map(Result::geocode)
                .map(Geocode::location)
                .map(loc -> new Coordinates(loc.latitude(), loc.longitude()));

        // Variante 2: location.latLng con lat/lng
        return direct.orElseGet(() -> opt(response)
                .map(GoogleAddressResponse::result)
                .map(Result::geocode)
                .map(Geocode::location)
                .map(Location::latLng)
                .map(latLng -> new Coordinates(latLng.latitude(), latLng.longitude()))
                .orElse(null));
    }

    private static <T> Optional<T> opt(T v) {
        return Optional.ofNullable(v);
    }
}
