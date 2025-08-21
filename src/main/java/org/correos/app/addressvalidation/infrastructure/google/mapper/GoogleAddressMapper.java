package org.correos.app.addressvalidation.infrastructure.google.mapper;

import org.correos.app.addressvalidation.domain.model.AddressStatusCode;
import org.correos.app.addressvalidation.domain.model.Coordinates;
import org.correos.app.addressvalidation.domain.model.NextAction;
import org.correos.app.addressvalidation.domain.model.ValidatedAddress;
import org.correos.app.addressvalidation.infrastructure.google.dto.response.*;
import org.correos.app.addressvalidation.infrastructure.google.util.NextActionMessageResolver;
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

        var action  = NextAction.fromString(nextActionCode);
        var message = NextActionMessageResolver.resolve(action);

        return new ValidatedAddress(
                formatted,
                locality,
                postal,
                action,
                message,
                "PREMISE".equalsIgnoreCase(granularity),
                List.of(),
                coords,
                AddressStatusCode.SUCCESS
        );
    }

    /**
     * Devuelve Coordinates soportando dos variantes de Google:
     * 1) location.latitude / location.longitude
     * 2) location.latLng.latitude / location.latLng.longitude
     */
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

    private static <T> Optional<T> opt(T v) { return Optional.ofNullable(v); }
}
