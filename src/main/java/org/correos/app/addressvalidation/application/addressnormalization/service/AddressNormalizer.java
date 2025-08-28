package org.correos.app.addressvalidation.application.addressnormalization.service;

import org.correos.app.addressvalidation.application.model.AddressValidationInput;
import org.correos.app.addressvalidation.domain.model.NormalizedAddress;

public interface AddressNormalizer {
    NormalizedAddress normalize(AddressValidationInput rawAddress);
}
