package org.correos.app.addressvalidation.infrastructure.google.mapper.logic;

import org.correos.app.addressvalidation.domain.model.GeocodeModality;
import org.correos.app.addressvalidation.domain.model.NextAction;
import org.correos.app.addressvalidation.infrastructure.google.util.GoogleNextActionMessageResolver;
import org.springframework.stereotype.Component;

@Component
public class NextActionDecider {

    public record Outcome(NextAction action, String message, boolean isValid) {}

    /**
     * Mantiene exactamente la misma lógica:
     * - strong si: modalidad PORTAL + reliability >= 85 + CP y localidad no vacíos
     * - action/message a partir de strong o del possibleNextAction
     * - isValid = strong || (PORTAL && reliability >= 85)
     */
    public Outcome decide(GeocodeModality modality,
                          int reliability,
                          String cpFinal,
                          String locFinal,
                          String nextActionCode) {

        boolean strong = (modality == GeocodeModality.PORTAL)
                && (reliability >= 85)
                && notBlank(cpFinal)
                && notBlank(locFinal);

        NextAction action = strong ? NextAction.ACCEPT : NextAction.fromString(nextActionCode);
        String message = strong ? "Dirección válida." : GoogleNextActionMessageResolver.resolve(action);
        boolean isValid = strong || (modality == GeocodeModality.PORTAL && reliability >= 85);

        return new Outcome(action, message, isValid);
    }

    private boolean notBlank(String s) {
        return s != null && !s.isBlank();
    }
}
