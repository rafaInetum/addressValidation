package org.correos.app.addressvalidation.application.model;

public record AddressValidationInput(

        /* Código único del envío  */
        String codEnvio,

        /* Dirección en texto plano */
        String originalAddress,

        /* Hint de idioma/localización: "es", "pt", etc. */
        String localeHint,

        /* Si ha sido corregida manualmente por un operador */
        boolean manuallyFixed,

        /* Código del sistema origen (ej. 1=ORION, 2=CORELAT...) */
        int codSistemaOrigen

) {}
