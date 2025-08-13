package org.correos.app.addressvalidation.infrastructure.google.dto.request;

import java.util.List;

public record AddressInput(
        String regionCode,
        String locality,
        String postalCode,
        List<String> addressLines
) {}
