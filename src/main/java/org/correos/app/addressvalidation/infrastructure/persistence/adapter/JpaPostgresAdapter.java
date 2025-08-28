package org.correos.app.addressvalidation.infrastructure.persistence.adapter;

import org.correos.app.addressvalidation.application.port.out.AddressPersistencePort;
import org.correos.app.addressvalidation.domain.model.ValidatedAddress;
import org.springframework.stereotype.Component;

/**
 * Adaptador de persistencia PostgreSQL
 */
@Component
public class JpaPostgresAdapter implements AddressPersistencePort {

    @Override
    public void save(ValidatedAddress address) {}
}
