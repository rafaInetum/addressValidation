package org.correos.app.addressvalidation.domain.model;

public record GeocodeInfo(
        Coordinates coordinates,
        GeocodeModality modality,
        Integer reliabilityPct,
        String source
) {}