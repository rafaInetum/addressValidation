package org.correos.app.addressvalidation.api.dto;

import java.util.List;

public record AddressRequestDto(
        String regionCode,
        String locality,
        String postalCode,
        List<String> addressLines
) {}
