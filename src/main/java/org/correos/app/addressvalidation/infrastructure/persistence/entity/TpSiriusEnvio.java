package org.correos.app.addressvalidation.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter @Setter @NoArgsConstructor
@Entity
@Table(name = "tp_sirius_envio")
public class TpSiriusEnvio {

    @Id
    @Column(name = "cod_envio", length = 23, nullable = false)
    private String codEnvio;

    @Column(name = "fec_actualizacion")       private OffsetDateTime fecActualizacion;
    @Column(name = "ind_intervencion_manual") private Boolean indIntervencionManual;
    @Column(name = "fec_creacion")            private OffsetDateTime fecCreacion;
    @Column(name = "cod_sistema_origen")      private Integer codSistemaOrigen;

}
