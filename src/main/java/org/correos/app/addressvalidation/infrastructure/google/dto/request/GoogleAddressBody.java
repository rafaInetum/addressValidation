package org.correos.app.addressvalidation.infrastructure.google.dto.request;

import java.util.List;

public record GoogleAddressBody(
        List<String> addressLines
) {}
