package org.correos.app.addressvalidation.infrastructure.google.mapper;

import org.correos.app.addressvalidation.application.model.AddressToValidate;
import org.correos.app.addressvalidation.infrastructure.google.dto.request.AddressValidationInputForGoogle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GoogleAddressRequestMapper {

    @Mapping(target = "regionCode", source = "regionCode")
    @Mapping(target = "locality", source = "locality")
    @Mapping(target = "postalCode", source = "postalCode")
    @Mapping(target = "addressLines", source = "addressLines")
    AddressValidationInputForGoogle toGoogleInput(AddressToValidate address);
}
