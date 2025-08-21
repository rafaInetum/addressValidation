package org.correos.app.addressvalidation.domain.service;

import org.correos.app.addressvalidation.application.model.RawAddressToValidate;
import org.correos.app.addressvalidation.domain.model.NormalizedAddress;

public interface AddressNormalizerService {
    NormalizedAddress normalize(RawAddressToValidate input);
}
