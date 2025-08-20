package org.correos.app.addressvalidation.api.mapper;

import org.correos.app.addressvalidation.api.dto.AddressRequestDto;
import org.correos.app.addressvalidation.application.model.AddressToValidate;

import java.util.List;

public class AddressDtoMapper {

    public static AddressToValidate toModel(AddressRequestDto dto) {
        return new AddressToValidate(
                dto.regionCode(),
                dto.locality(),
                dto.postalCode(),
                dto.addressLines()
        );
    }

    public static List<AddressToValidate> toModelList(List<AddressRequestDto> dtos) {
        return dtos.stream()
                .map(AddressDtoMapper::toModel)
                .toList();
    }
}
