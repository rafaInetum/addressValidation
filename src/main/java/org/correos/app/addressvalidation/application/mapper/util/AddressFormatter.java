package org.correos.app.addressvalidation.application.mapper.util;

import org.correos.app.addressvalidation.domain.model.NormalizedAddress;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AddressFormatter {

    @Named("buildAddressLines")
    public List<String> format(NormalizedAddress n) {
        StringBuilder sb = new StringBuilder();
        if (n.streetType() != null) sb.append(n.streetType()).append(" ");
        if (n.streetName() != null) sb.append(n.streetName()).append(" ");
        if (n.streetNumber() != null) sb.append(n.streetNumber()).append(" ");
        if (n.floor() != null) sb.append("PLANTA ").append(n.floor()).append(" ");
        if (n.door() != null) sb.append("PUERTA ").append(n.door());
        return List.of(sb.toString().trim());
    }
}
