package org.correos.app.addressvalidation.infrastructure.google.mapper.request;

import lombok.RequiredArgsConstructor;
import org.correos.app.addressvalidation.application.model.AddressToValidate;
import org.correos.app.addressvalidation.infrastructure.google.dto.request.GoogleAddressBody;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public final class GoogleAddressRequestMapper {

    public GoogleAddressBody toGoogleInput(AddressToValidate address) {
        if (address == null) return null;
        return new GoogleAddressBody(List.of(address.originalAddress()));
    }
}
