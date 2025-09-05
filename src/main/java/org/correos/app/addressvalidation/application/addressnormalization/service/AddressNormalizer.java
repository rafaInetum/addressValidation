package org.correos.app.addressvalidation.application.addressnormalization.service;

import org.correos.app.addressvalidation.application.model.AddressValidationInput;
import org.correos.app.addressvalidation.domain.model.NormalizedAddress;
import org.correos.app.addressvalidation.domain.model.ValidatedAddress;

public interface AddressNormalizer {
    NormalizedAddress normalize(ValidatedAddress validated);
}
