package org.correos.app.addressvalidation.application.addressnormalization.service;

import org.correos.app.addressvalidation.domain.model.NormalizedAddress;
import org.correos.app.addressvalidation.domain.model.RawAddress;

public interface AddressNormalizer {
    NormalizedAddress normalize(RawAddress input);
}
