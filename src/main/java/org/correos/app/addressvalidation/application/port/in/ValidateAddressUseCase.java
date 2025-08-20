package org.correos.app.addressvalidation.application.port.in;

import org.correos.app.addressvalidation.application.model.AddressToValidate;
import org.correos.app.addressvalidation.domain.model.ValidatedAddress;
import org.correos.app.addressvalidation.infrastructure.google.dto.request.AddressInput;

import java.util.List;

public interface ValidateAddressUseCase {
    List<ValidatedAddress> execute(List<AddressToValidate> addresses);
}
