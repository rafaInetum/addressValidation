package org.correos.app.addressvalidation.api.controller;

import org.correos.app.addressvalidation.api.dto.*;
import org.correos.app.addressvalidation.api.mapper.AddressDtoMapper;
import org.correos.app.addressvalidation.application.model.RawAddressToValidate;
import org.correos.app.addressvalidation.application.port.in.ValidateAddressUseCase;
import org.correos.app.addressvalidation.domain.model.ValidatedAddress;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/address")
public class AddressValidationController {

    private final ValidateAddressUseCase validateAddressUseCase;

    public AddressValidationController(ValidateAddressUseCase validateAddressUseCase) {
        this.validateAddressUseCase = validateAddressUseCase;
    }

    @PostMapping("/validate")
    public ResponseEntity<List<ValidatedAddressResponseDto>> validateAddresses(@RequestBody List<AddressRequestDto> dtos) {
        List<RawAddressToValidate> toValidate = AddressDtoMapper.toModelList(dtos);
        List<ValidatedAddress> results = validateAddressUseCase.execute(toValidate);
        return ResponseEntity.ok(AddressDtoMapper.toDtoList(results));
    }
}
