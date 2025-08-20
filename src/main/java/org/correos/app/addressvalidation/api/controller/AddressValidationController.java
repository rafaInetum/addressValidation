package org.correos.app.addressvalidation.api.controller;

import org.correos.app.addressvalidation.api.dto.AddressRequestDto;
import org.correos.app.addressvalidation.api.mapper.AddressDtoMapper;
import org.correos.app.addressvalidation.application.model.AddressToValidate;
import org.correos.app.addressvalidation.domain.model.ValidatedAddress;
import org.correos.app.addressvalidation.application.port.in.ValidateAddressUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @PostMapping("/validation")
    public ResponseEntity<List<ValidatedAddress>> validateAddresses(@RequestBody List<AddressRequestDto> dtos) {
        List<AddressToValidate> addresses = AddressDtoMapper.toModelList(dtos);
        return ResponseEntity.ok(validateAddressUseCase.execute(addresses));
    }

}
