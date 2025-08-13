package org.correos.app.addressvalidation.api.controller;

import org.correos.app.addressvalidation.domain.model.ValidatedAddress;
import org.correos.app.addressvalidation.domain.port.in.ValidateAddressUseCase;
import org.correos.app.addressvalidation.infrastructure.google.dto.request.AddressInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressValidationController {

    private final ValidateAddressUseCase validateAddressUseCase;

    private static final Logger logger = (Logger) LoggerFactory.getLogger(AddressValidationController.class);

    public AddressValidationController(ValidateAddressUseCase validateAddressUseCase) {
        this.validateAddressUseCase = validateAddressUseCase;
    }

    @PostMapping("/validation")
    public ResponseEntity<List<ValidatedAddress>> validateAddresses(@RequestBody List<AddressInput> addressInputs) {
        return ResponseEntity.ok(validateAddressUseCase.execute(addressInputs));
    }
}
