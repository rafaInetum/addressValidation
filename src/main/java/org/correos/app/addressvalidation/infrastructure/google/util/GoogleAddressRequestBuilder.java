package org.correos.app.addressvalidation.infrastructure.google.util;

import org.correos.app.addressvalidation.infrastructure.google.dto.request.AddressValidationInputForGoogle;

import java.util.List;

public class GoogleAddressRequestBuilder {

    public static String buildJsonRequest(AddressValidationInputForGoogle addressInput) {
        return """
    {
      "address": {
        "regionCode": "%s",
        "locality": "%s",
        "postalCode": "%s",
        "addressLines": [%s]
      }
    }
    """.formatted(
                addressInput.regionCode(),
                addressInput.locality(),
                addressInput.postalCode(),
                buildAddressLines(addressInput.addressLines())
        );
    }

    private static String buildAddressLines(List<String> lines) {
        return lines.stream()
                .map(line -> "\"" + line + "\"")
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
    }

}

