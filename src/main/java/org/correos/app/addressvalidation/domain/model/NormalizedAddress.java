package org.correos.app.addressvalidation.domain.model;

import java.util.Map;

public record NormalizedAddress(
        String tipoVia,
        String nombreVia,
        String numero,
        String planta,
        String puerta,
        String codigoPostal,
        String localidad,
        String provincia,
        String pais,
        String observaciones,
        Map<String,String> extras,
        double confianza,
        String locale
) {}
