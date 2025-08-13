package org.correos.app.addressvalidation.infrastructure.google.util;

import org.correos.app.addressvalidation.infrastructure.google.dto.response.NextAction;

public class NextActionMessageResolver {

    public static String resolve(NextAction action) {
        return switch (action) {
            case ACCEPT -> "Dirección confirmada por Google.";
            case REVIEW -> "Dirección incompleta o ambigua.";
            case CONFIRM -> "Google sugiere confirmación de la dirección.";
            case FIX, UNKNOWN -> "Estado desconocido. Requiere revisión.";
        };
    }
}
