package org.correos.app.addressvalidation.api.mapper;

import org.correos.app.addressvalidation.api.dto.response.ValidatedAddressResponseDTO;
import org.correos.app.addressvalidation.domain.model.ValidatedAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AddressResponseMapper {

    @Mapping(target = "address", source = "validatedAddress")
    @Mapping(target = "validationStatus", source = "validatedAddress.nextAction")
    ValidatedAddressResponseDTO toDTO(ValidatedAddress validatedAddress);

    List<ValidatedAddressResponseDTO> tolistDTO(List<ValidatedAddress> validatedAddresses);
}
