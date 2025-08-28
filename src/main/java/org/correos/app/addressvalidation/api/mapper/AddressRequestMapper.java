package org.correos.app.addressvalidation.api.mapper;

import org.correos.app.addressvalidation.api.dto.request.AddressValidationRequestDto;
import org.correos.app.addressvalidation.application.model.AddressValidationInput;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AddressRequestMapper {

    AddressValidationInput toModel(AddressValidationRequestDto dto);
    List<AddressValidationInput> toModelList(List<AddressValidationRequestDto> dtos);
}
