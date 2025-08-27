package org.correos.app.addressvalidation.application.mapper;

import org.correos.app.addressvalidation.application.model.RawAddressToValidate;
import org.correos.app.addressvalidation.domain.model.RawAddress;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RawAddressMapper {
    RawAddress toDomain(RawAddressToValidate dto);
}
