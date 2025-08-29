package org.correos.app.addressvalidation.api.dto.request;

/**
 * DTO que representa una dirección a validar.
 */
public record AddressValidationRequestDTO(

        /** Código único del envío  */
        String codEnvio,

        /** Dirección en texto plano */
        String addressPlainText,

        /** Hint de idioma/localización: "es", "pt", etc. */
        String localeHint,

        /** Si ha sido corregida manualmente por un operador */
        boolean manuallyFixed,

        /** Código del sistema origen (ej. 1=ORION, 2=CORELAT...) */
        int codSistemaOrigen

) {}
