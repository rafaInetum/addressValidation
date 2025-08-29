package org.correos.app.addressvalidation.application.service;


import org.correos.app.addressvalidation.application.model.AddressToValidate;
import org.correos.app.addressvalidation.application.model.AddressValidationInput;
import org.correos.app.addressvalidation.application.port.in.CompleteAddressUseCase;
import org.correos.app.addressvalidation.application.port.out.AutocompletePort;
import org.correos.app.addressvalidation.application.port.out.PlaceDetailsPort;
import org.correos.app.addressvalidation.domain.model.ValidatedAddress;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CompleteAddressService implements CompleteAddressUseCase {

    private final AutocompletePort autocomplete;
    private final PlaceDetailsPort placeDetails;

    public CompleteAddressService(AutocompletePort autocomplete,
                                  PlaceDetailsPort placeDetails) {
        this.autocomplete = autocomplete;
        this.placeDetails = placeDetails;
    }

    public List<String> findSuggestions(AddressToValidate address) {
        List<String> placeIds = autocomplete.fetchPlaceIds(address);
        return placeDetails.getDetailedPlaces(placeIds);
    }

}
