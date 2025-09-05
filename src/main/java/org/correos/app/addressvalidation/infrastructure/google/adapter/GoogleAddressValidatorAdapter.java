package org.correos.app.addressvalidation.infrastructure.google.adapter;

import lombok.RequiredArgsConstructor;
import org.correos.app.addressvalidation.application.model.AddressToValidate;
import org.correos.app.addressvalidation.application.port.out.AddressValidationPort;
import org.correos.app.addressvalidation.domain.model.NormalizedAddress;
import org.correos.app.addressvalidation.domain.model.ValidatedAddress;
import org.correos.app.addressvalidation.infrastructure.google.client.GoogleAddressValidationClient;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class GoogleAddressValidatorAdapter implements AddressValidationPort {

    private final GoogleAddressValidationClient client;

    @Override
    public ValidatedAddress requestValidation(AddressToValidate address) {
        return client.requestValidation(address);
    }
}
