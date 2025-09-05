package org.correos.app.addressvalidation.application.mapper;

import lombok.RequiredArgsConstructor;
import org.correos.app.addressvalidation.application.model.AddressToValidate;
import org.correos.app.addressvalidation.application.model.AddressValidationInput;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class AddressToValidateBuilder {

    public AddressToValidate buildAddressToValidate(AddressValidationInput input) {

        // === Si el input es null, devolvemos null
        if (input == null) {
            return null;
        }

        // === Mantenemos la direccion original introducida
        return new AddressToValidate(input.originalAddress());
    }
}














//package org.correos.app.addressvalidation.application.mapper;
//
//import org.correos.app.addressvalidation.application.mapper.util.RegionCodeNormalizer;
//import org.correos.app.addressvalidation.application.mapper.util.AddressFormatter;
//import org.correos.app.addressvalidation.application.model.AddressToValidate;
//import org.correos.app.addressvalidation.domain.model.NormalizedAddress;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//
//@Mapper(
//        componentModel = "spring",
//        uses = {RegionCodeNormalizer.class, AddressFormatter.class}
//)
//public interface NormalizedAddressToValidateMapper {
//
//    @Mapping(source = "country", target = "regionCode", qualifiedByName = "normalizeRegionCode")
//    @Mapping(source = ".", target = "addressLines", qualifiedByName = "buildAddressLines")
//    @Mapping(source = "locality", target = "locality")
//    AddressToValidate toStructuredAddress(NormalizedAddress n);
//}
