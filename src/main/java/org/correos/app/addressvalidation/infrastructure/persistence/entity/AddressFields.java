package org.correos.app.addressvalidation.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;
import java.math.BigDecimal;

/** Campos comunes compartidos por tb_sirius_direccion y tb_sirius_direccion_replica */
@MappedSuperclass
@Getter @Setter
public abstract class AddressFields {

    @Column(name = "estado_direccion", length = 50) protected String estadoDireccion;

    @Column(name = "cod_pais", length = 3) protected String codPais;
    @Column(name = "nom_localidad_orig", length = 100) protected String nomLocalidadOrig;
    @Column(name = "cod_postal_orig", length = 10) protected String codPostalOrig;

    @Column(name = "direccion_orig", length = 200) protected String direccionOrig;
    @Column(name = "dir_complemento", length = 200) protected String dirComplemento;

    @Column(name = "direccion_validada", length = 200) protected String direccionValidada;
    @Column(name = "direccion_autocompletada", length = 200) protected String direccionAutocompletada;
    @Column(name = "direccion_corregida", length = 200) protected String direccionCorregida;
    @Column(name = "direccion_formateada", length = 200) protected String direccionFormateada;

    @Column(name = "variante", length = 1) protected String variante;

    @Column(name = "cod_localidad", length = 13) protected String codLocalidad;
    @Column(name = "granularidad_localidad", length = 10) protected String granularidadLocalidad;

    @Column(name = "cod_postal", length = 10) protected String codPostal;
    @Column(name = "granularidad_cod_postal", length = 10) protected String granularidadCodPostal;

    @Column(name = "barrio", length = 50) protected String barrio;

    @Column(name = "cod_tipo_via", length = 3) protected String codTipoVia;
    @Column(name = "nom_via", length = 200) protected String nomVia;

    @Column(name = "numero") protected Integer numero;
    @Column(name = "granularidad_numero", length = 50) protected String granularidadNumero;
    @Column(name = "num_inf") protected Integer numInf;
    @Column(name = "num_sup") protected Integer numSup;

    @Column(name = "calific", length = 3) protected String calific;
    @Column(name = "calific_inf", length = 3) protected String calificInf;
    @Column(name = "calific_sup", length = 3) protected String calificSup;

    @Column(name = "km") protected Integer km;
    @Column(name = "hm") protected Integer hm;

    @Column(name = "bloque", length = 2) protected String bloque;
    @Column(name = "portal", length = 2) protected String portal;
    @Column(name = "escalera", length = 3) protected String escalera;
    @Column(name = "planta", length = 3) protected String planta;
    @Column(name = "puerta", length = 4) protected String puerta;

    // Coordenadas
    @Column(name = "latitud_wgs84",  precision = 18, scale = 11) protected BigDecimal latitudWgs84;
    @Column(name = "longitud_wgs84", precision = 18, scale = 11) protected BigDecimal longitudWgs84;
    @Column(name = "latitud_etrs89",  precision = 18, scale = 11) protected BigDecimal latitudEtrs89;
    @Column(name = "longitud_etrs89", precision = 18, scale = 11) protected BigDecimal longitudEtrs89;

    @Column(name = "porcentaje_fiabilidad", precision = 5, scale = 2) protected BigDecimal porcentajeFiabilidad;

    @Column(name = "refcatparc", length = 14) protected String refcatparc;
    @Column(name = "refcatbi", length = 20) protected String refcatbi;

    // PostGIS: geometry(Point,4326)
    @Column(name = "geometria", columnDefinition = "geometry(Point,4326)")
    protected Point geometria;

    @Column(name = "codigo_recalculo", length = 50) protected String codigoRecalculo;
    @Column(name = "ind_direccion_norm") protected Boolean indDireccionNorm;
    @Column(name = "cod_origen", length = 23) protected String codOrigen;

}
