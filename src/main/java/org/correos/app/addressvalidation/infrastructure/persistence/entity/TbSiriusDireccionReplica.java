package org.correos.app.addressvalidation.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter @Setter @NoArgsConstructor
@Entity
@Table(
        name = "tb_sirius_direccion_replica",
        indexes = { @Index(name = "idx_direcciones_cod_envio_replica", columnList = "cod_envio") }
)
public class TbSiriusDireccionReplica extends AddressFields {

    @EmbeddedId
    private ReplicaPk id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cod_envio", referencedColumnName = "cod_envio", nullable = false)
    private TpSiriusEnvio envio;

    /** PK compuesta: id_direccion + fce_cambio */
    @Embeddable
    public static class ReplicaPk implements java.io.Serializable{
        @Column(name = "id_direccion", nullable = false)
        private Long idDireccion;

        @Column(name = "fce_cambio", nullable = false)
        private OffsetDateTime fceCambio;

        public ReplicaPk() {}
        public ReplicaPk(Long idDireccion, OffsetDateTime fceCambio) {
            this.idDireccion = idDireccion; this.fceCambio = fceCambio;
        }
        public Long getIdDireccion() { return idDireccion; }
        public void setIdDireccion(Long idDireccion) { this.idDireccion = idDireccion; }
        public OffsetDateTime getFceCambio() { return fceCambio; }
        public void setFceCambio(OffsetDateTime fceCambio) { this.fceCambio = fceCambio; }

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ReplicaPk pk)) return false;
            return java.util.Objects.equals(idDireccion, pk.idDireccion)
                    && java.util.Objects.equals(fceCambio, pk.fceCambio);
        }
        @Override public int hashCode() {
            return java.util.Objects.hash(idDireccion, fceCambio);
        }
    }
}
