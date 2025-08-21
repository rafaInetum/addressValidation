package org.correos.app.addressvalidation.api.mapper;

import org.correos.app.addressvalidation.api.dto.AddressRequestDto;
import org.correos.app.addressvalidation.api.dto.ValidatedAddressResponseDto;
import org.correos.app.addressvalidation.application.model.RawAddressToValidate;
import org.correos.app.addressvalidation.domain.model.ValidatedAddress;

import java.util.List;
import java.util.stream.Collectors;

public class AddressDtoMapper {

    public static List<RawAddressToValidate> toModelList(List<AddressRequestDto> dtos) {
        return dtos.stream().map(AddressDtoMapper::toModel).collect(Collectors.toList());
    }

    public static RawAddressToValidate toModel(AddressRequestDto dto) {
        return new RawAddressToValidate(dto.rawText(), dto.localeHint());
    }

    public static ValidatedAddressResponseDto toDto(ValidatedAddress model) {
        return new ValidatedAddressResponseDto(model, model.nextAction());
    }

    public static List<ValidatedAddressResponseDto> toDtoList(List<ValidatedAddress> models) {
        return models.stream()
                .map(AddressDtoMapper::toDto)
                .collect(Collectors.toList());
    }

}
