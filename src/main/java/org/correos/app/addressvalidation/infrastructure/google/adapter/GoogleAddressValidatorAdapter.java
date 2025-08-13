package org.correos.app.addressvalidation.infrastructure.google.adapter;

import org.correos.app.addressvalidation.domain.model.ValidatedAddress;
import org.correos.app.addressvalidation.domain.port.out.AddressValidationPort;
import org.correos.app.addressvalidation.infrastructure.google.client.GoogleAddressValidationClient;
import org.correos.app.addressvalidation.infrastructure.google.dto.request.AddressInput;
import org.correos.app.addressvalidation.infrastructure.google.dto.response.GoogleAddressResponse;
import org.correos.app.addressvalidation.infrastructure.google.mapper.GoogleAddressMapper;
import org.springframework.stereotype.Component;

@Component
public class GoogleAddressValidatorAdapter implements AddressValidationPort {

    private final GoogleAddressValidationClient client;
    private final GoogleAddressMapper mapper;

    public GoogleAddressValidatorAdapter(GoogleAddressValidationClient client, GoogleAddressMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    @Override
    public ValidatedAddress requestValidation(AddressInput address) {
        GoogleAddressResponse googleResponse = client.validate(address);
        return mapper.toDomain(googleResponse);
    }
}
