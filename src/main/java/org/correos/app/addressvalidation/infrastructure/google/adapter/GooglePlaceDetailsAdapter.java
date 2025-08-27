package org.correos.app.addressvalidation.infrastructure.google.adapter;

import org.correos.app.addressvalidation.application.port.out.PlaceDetailsPort;
import org.correos.app.addressvalidation.infrastructure.google.client.GooglePlaceDetailsClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GooglePlaceDetailsAdapter implements PlaceDetailsPort {

    private final GooglePlaceDetailsClient client;

    public GooglePlaceDetailsAdapter(GooglePlaceDetailsClient client) {
        this.client = client;
    }

    @Override
    public List<String> getDetailedPlaces(List<String> placeIds) {
        return client.fetchFormattedAddresses(placeIds);
    }
}
