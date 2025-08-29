package org.correos.app.addressvalidation.infrastructure.persistence.repository;

import org.correos.app.addressvalidation.infrastructure.persistence.entity.TbSiriusDireccionReplica;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TbSiriusDireccionReplicaRepository
        extends JpaRepository<TbSiriusDireccionReplica, TbSiriusDireccionReplica.ReplicaPk> {}