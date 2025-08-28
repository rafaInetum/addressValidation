package org.correos.app.addressvalidation.infrastructure.google.mapper.response.logic;

import org.correos.app.addressvalidation.domain.model.GeocodeModality;
import org.correos.app.addressvalidation.infrastructure.google.dto.response.Result;
import org.correos.app.addressvalidation.infrastructure.google.dto.response.Verdict;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GoogleModalityResolver {

    public GeocodeModality resolve(Result result) {
        String granularity = Optional.ofNullable(result)
                .map(Result::verdict)
                .map(Verdict::validationGranularity)
                .map(String::toUpperCase)
                .orElse("");

        return switch (granularity) {
            case "PREMISE", "SUB_PREMISE" -> GeocodeModality.PORTAL;
            case "ROUTE" -> GeocodeModality.APROX_PORTAL;
            default -> GeocodeModality.CALLE;
        };
    }
}
