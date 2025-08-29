package org.correos.app.addressvalidation.infrastructure.persistence.repository;

import org.correos.app.addressvalidation.infrastructure.persistence.entity.TbSiriusDireccion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TbSiriusDireccionRepository
        extends JpaRepository<TbSiriusDireccion, Long> {}