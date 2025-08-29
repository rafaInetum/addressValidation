package org.correos.app.addressvalidation.infrastructure.persistence.repository;

import org.correos.app.addressvalidation.infrastructure.persistence.entity.TpSiriusEnvio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TpSiriusEnvioRepository
        extends JpaRepository<TpSiriusEnvio, String> {}
