package org.correos.app.addressvalidation.infrastructure.google.mapper.logic;

import org.correos.app.addressvalidation.domain.model.GeocodeModality;
import org.correos.app.addressvalidation.infrastructure.google.dto.response.Geocode;
import org.correos.app.addressvalidation.infrastructure.google.dto.response.Result;
import org.correos.app.addressvalidation.infrastructure.google.dto.response.Verdict;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ReliabilityCalculator {

    public int calculate(Result result, GeocodeModality modality) {
        int reliability = switch (modality) {
            case PORTAL -> {
                double meters = Optional.ofNullable(result)
                        .map(Result::geocode)
                        .map(Geocode::featureSizeMeters)
                        .orElse(50.0);
                yield meters < 30.0 ? 95 : 85;
            }
            case APROX_PORTAL -> 85;
            case CALLE -> 70;
            case MANUAL_FIX -> 100;
        };

        boolean unconfirmed = Optional.ofNullable(result)
                .map(Result::verdict)
                .map(Verdict::hasUnconfirmedComponents)
                .orElse(false);

        if (unconfirmed) {
            reliability = Math.max(0, reliability - 10);
        }

        return reliability;
    }
}
