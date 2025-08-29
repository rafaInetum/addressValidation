package org.correos.app.addressvalidation.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.OffsetDateTime;

@Getter @Setter @NoArgsConstructor
@Entity
@Table(name = "tp_sirius_api_google_log")
public class TpSiriusApiGoogleLog {

    @EmbeddedId
    private LogPk id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "resultado_jsonb", columnDefinition = "jsonb")
    private String resultadoJson;

    @Column(name = "ind_exito") private Boolean indExito;

    @Column(name = "mensaje_error", length = 200)
    private String mensajeError;

    /** PK compuesta: cod_envio + fec_log + tipo_api */
    @Embeddable
    public static class LogPk implements java.io.Serializable{
        @Column(name = "cod_envio", length = 23, nullable = false)
        private String codEnvio;

        @Column(name = "fec_log", nullable = false)
        private OffsetDateTime fecLog;

        @Column(name = "tipo_api", length = 50, nullable = false)
        private String tipoApi;

        public LogPk() {}
        public LogPk(String codEnvio, OffsetDateTime fecLog, String tipoApi) {
            this.codEnvio = codEnvio; this.fecLog = fecLog; this.tipoApi = tipoApi;
        }

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof LogPk pk)) return false;
            return java.util.Objects.equals(codEnvio, pk.codEnvio)
                    && java.util.Objects.equals(fecLog, pk.fecLog)
                    && java.util.Objects.equals(tipoApi, pk.tipoApi);
        }
        @Override public int hashCode() {
            return java.util.Objects.hash(codEnvio, fecLog, tipoApi);
        }
    }
}
