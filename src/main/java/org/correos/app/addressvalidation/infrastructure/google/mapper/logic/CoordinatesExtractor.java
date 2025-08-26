package org.correos.app.addressvalidation.infrastructure.google.mapper.logic;

import org.correos.app.addressvalidation.domain.model.Coordinates;
import org.correos.app.addressvalidation.infrastructure.google.dto.response.*;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CoordinatesExtractor {

    public Coordinates extract(GoogleAddressResponse response) {
        var direct = Optional.ofNullable(response).map(GoogleAddressResponse::result)
                .map(Result::geocode).map(Geocode::location)
                .map(loc -> new Coordinates(loc.latitude(), loc.longitude()));
        return direct.orElseGet(() -> Optional.ofNullable(response).map(GoogleAddressResponse::result)
                .map(Result::geocode).map(Geocode::location)
                .map(Location::latLng)
                .map(latLng -> new Coordinates(latLng.latitude(), latLng.longitude()))
                .orElse(null));
    }
}
