package org.correos.app.addressvalidation.application.model;

import java.util.List;

public record AddressToValidate(
        String regionCode,
        String locality,
        String postalCode,
        List<String> addressLines
) {}