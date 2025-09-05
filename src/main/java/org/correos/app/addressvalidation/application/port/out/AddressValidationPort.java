package org.correos.app.addressvalidation.application.port.out;

import org.correos.app.addressvalidation.application.model.AddressToValidate;
import org.correos.app.addressvalidation.domain.model.ValidatedAddress;

public interface AddressValidationPort {
    ValidatedAddress requestValidation(AddressToValidate address);
}
