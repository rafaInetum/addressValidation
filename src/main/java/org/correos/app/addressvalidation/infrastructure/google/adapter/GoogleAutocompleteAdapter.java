package org.correos.app.addressvalidation.infrastructure.google.adapter;


import org.correos.app.addressvalidation.application.model.AddressToValidate;
import org.correos.app.addressvalidation.application.port.out.AutocompletePort;
import org.correos.app.addressvalidation.infrastructure.google.client.GoogleAutocompleteClient;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Primary
public class GoogleAutocompleteAdapter implements AutocompletePort {

    private final GoogleAutocompleteClient client;

    public GoogleAutocompleteAdapter(GoogleAutocompleteClient client) {
        this.client = client;
    }

    @Override
    public List<String> fetchPlaceIds(AddressToValidate input) {
        return client.searchPlaceIds(input);
    }

    @Override
    public List<String> findSuggestions(String address) {
        return client.searchSuggestions(address);
    }

}
