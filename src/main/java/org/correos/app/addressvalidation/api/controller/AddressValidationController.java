package org.correos.app.addressvalidation.api.controller;

import org.correos.app.addressvalidation.api.dto.request.AddressValidationRequestDto;
import org.correos.app.addressvalidation.api.dto.response.ValidatedAddressResponseDto;
import org.correos.app.addressvalidation.api.mapper.AddressRequestMapper;
import org.correos.app.addressvalidation.api.mapper.AddressResponseMapper;
import org.correos.app.addressvalidation.application.model.AddressValidationInput;
import org.correos.app.addressvalidation.application.port.in.ValidateAddressUseCase;
import org.correos.app.addressvalidation.domain.model.ValidatedAddress;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressValidationController {

    private final ValidateAddressUseCase validateAddressUseCase;
    private final AddressRequestMapper addressRequestMapper;
    private final AddressResponseMapper addressResponseMapper;

    public AddressValidationController(
            ValidateAddressUseCase validateAddressUseCase,
            AddressRequestMapper addressRequestMapper,
            AddressResponseMapper validatedAddressMapper
    ) {
        this.validateAddressUseCase = validateAddressUseCase;
        this.addressRequestMapper = addressRequestMapper;
        this.addressResponseMapper = validatedAddressMapper;
    }

    @PostMapping
    public ResponseEntity<List<ValidatedAddressResponseDto>> validateAddresses(@RequestBody List<AddressValidationRequestDto> addresses) {
        List<AddressValidationInput> toValidate = addressRequestMapper.toModelList(addresses);
        List<ValidatedAddress> results = validateAddressUseCase.execute(toValidate);
        return ResponseEntity.ok(addressResponseMapper.toDtoList(results));
    }
}
