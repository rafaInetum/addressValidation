package org.correos.app.addressvalidation.infrastructure.google.util;

import org.correos.app.addressvalidation.infrastructure.google.dto.request.GoogleAddressBody;

import java.util.List;

public class GoogleAddressRequestBuilder {

    public static String buildJsonRequest(GoogleAddressBody addressInput) {
        return """
    {
      "address": {
        "addressLines": [%s]
      }
    }
    """.formatted(
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

