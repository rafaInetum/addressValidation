package org.correos.app.addressvalidation.application.service;


import org.correos.app.addressvalidation.application.model.AddressToValidate;
import org.correos.app.addressvalidation.application.port.in.CompletedAddressUseCase;
import org.correos.app.addressvalidation.application.port.out.AutocompletePort;
import org.correos.app.addressvalidation.application.port.out.PlaceDetailsPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CompletedAddressService implements CompletedAddressUseCase {

    private final AutocompletePort autocomplete;
    private final PlaceDetailsPort placeDetails;

    public CompletedAddressService(AutocompletePort autocomplete,
                                   PlaceDetailsPort placeDetails) {
        this.autocomplete = autocomplete;
        this.placeDetails = placeDetails;
    }

    public List<String> execute(AddressToValidate address) {
        List<String> placeIds = autocomplete.fetchPlaceIds(address);
        return placeDetails.getDetailedPlaces(placeIds);
    }

}
