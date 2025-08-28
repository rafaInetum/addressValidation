package org.correos.app.addressvalidation.application.port.out;

import org.correos.app.addressvalidation.domain.model.ValidatedAddress;

public interface AddressPersistencePort {
    void save(ValidatedAddress address);
}
