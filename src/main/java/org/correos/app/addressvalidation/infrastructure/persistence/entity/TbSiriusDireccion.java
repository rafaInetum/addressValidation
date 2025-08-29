package org.correos.app.addressvalidation.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
@Entity
@Table(
        name = "tb_sirius_direccion",
        indexes = { @Index(name = "idx_direcciones_cod_envio", columnList = "cod_envio") }
)
public class TbSiriusDireccion extends AddressFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_direccion", nullable = false)
    private Long idDireccion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cod_envio", referencedColumnName = "cod_envio", nullable = false)
    private TpSiriusEnvio envio;

}
