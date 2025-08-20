package org.correos.app.addressvalidation.application.port.out;

import org.correos.app.addressvalidation.application.model.AddressToValidate;
import org.correos.app.addressvalidation.domain.model.ValidatedAddress;
import org.correos.app.addressvalidation.infrastructure.google.dto.request.AddressInput;

public interface AddressValidationPort {
    ValidatedAddress requestValidation(AddressToValidate address);
}
