package org.correos.app.addressvalidation.api.mapper;

import org.correos.app.addressvalidation.api.dto.response.ValidatedAddressResponseDto;
import org.correos.app.addressvalidation.domain.model.ValidatedAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AddressResponseMapper {

    @Mapping(target = "address", source = "validatedAddress")
    @Mapping(target = "validationStatus", source = "validatedAddress.nextAction")
    ValidatedAddressResponseDto toDto(ValidatedAddress validatedAddress);

    List<ValidatedAddressResponseDto> toDtoList(List<ValidatedAddress> validatedAddresses);
}
