package org.correos.app.addressvalidation.api.dto;

import org.correos.app.addressvalidation.domain.model.NextAction;
import org.correos.app.addressvalidation.domain.model.ValidatedAddress;

public record ValidatedAddressResponseDto(
        ValidatedAddress address,
        NextAction validationStatus
) {}
