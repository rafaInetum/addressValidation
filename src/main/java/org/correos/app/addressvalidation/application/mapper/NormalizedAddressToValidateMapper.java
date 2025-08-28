package org.correos.app.addressvalidation.application.mapper;

import org.correos.app.addressvalidation.application.mapper.util.RegionCodeNormalizer;
import org.correos.app.addressvalidation.application.mapper.util.AddressFormatter;
import org.correos.app.addressvalidation.application.model.AddressToValidate;
import org.correos.app.addressvalidation.domain.model.NormalizedAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {RegionCodeNormalizer.class, AddressFormatter.class}
)
public interface NormalizedAddressToValidateMapper {

    @Mapping(source = "country", target = "regionCode", qualifiedByName = "normalizeRegionCode")
    @Mapping(source = ".", target = "addressLines", qualifiedByName = "buildAddressLines")
    @Mapping(source = "locality", target = "locality")
    AddressToValidate toStructuredAddress(NormalizedAddress n);
}
