package org.correos.app.addressvalidation.infrastructure.persistence.repository;

import org.correos.app.addressvalidation.infrastructure.persistence.entity.TpSiriusApiGoogleLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TpSiriusApiGoogleLogRepository
        extends JpaRepository<TpSiriusApiGoogleLog, TpSiriusApiGoogleLog.LogPk> {}