package org.correos.app.addressvalidation.application.addressnormalization.model;

import java.util.Map;

public record Complements(
        String planta,
        String puerta,
        Map<String, String> extras,
        String observaciones
) {}