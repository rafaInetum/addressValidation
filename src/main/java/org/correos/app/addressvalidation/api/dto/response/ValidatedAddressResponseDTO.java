package org.correos.app.addressvalidation.api.dto.response;

import org.correos.app.addressvalidation.domain.model.NextAction;
import org.correos.app.addressvalidation.domain.model.ValidatedAddress;

public record ValidatedAddressResponseDTO(
        ValidatedAddress address,
        NextAction validationStatus
) {}
