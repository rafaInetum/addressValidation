package org.correos.app.addressvalidation.application.mapper;

import org.correos.app.addressvalidation.application.model.RawAddressToValidate;
import org.correos.app.addressvalidation.domain.model.RawAddress;

public class RawAddressMapper {
    public static RawAddress toDomain(RawAddressToValidate dto) {
        return new RawAddress(dto.rawText(), dto.localeHint(), dto.manuallyFixed());
    }
}
