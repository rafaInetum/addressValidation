package org.correos.app.addressvalidation.domain.addressnormalization.rule;

import org.correos.app.addressvalidation.domain.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GeocodeReliabilityAdjuster {

    private final int portalThreshold;

    public GeocodeReliabilityAdjuster(@Value("${geocode.portal.threshold:85}") int portalThreshold) {
        this.portalThreshold = portalThreshold;
    }

    public ValidatedAddress adjust(ValidatedAddress validated, boolean manuallyFixed) {
        GeocodeInfo geocode = validated.geocode();

        if (geocode == null) return validated;

        if (manuallyFixed) {
            GeocodeInfo adjusted = new GeocodeInfo(
                    geocode.coordinates(),
                    GeocodeModality.MANUAL_FIX,
                    100,
                    "MANUAL"
            );
            return validated.withGeocode(adjusted);
        }

        if (geocode.modality() == GeocodeModality.PORTAL &&
                (geocode.reliabilityPct() == null || geocode.reliabilityPct() < portalThreshold)) {

            GeocodeInfo adjusted = new GeocodeInfo(
                    geocode.coordinates(),
                    GeocodeModality.APROX_PORTAL,
                    geocode.reliabilityPct() == null ? portalThreshold - 1 : geocode.reliabilityPct(),
                    geocode.source()
            );
            return validated.withGeocode(adjusted);
        }

        return validated;
    }
}
