package org.correos.app.addressvalidation.application.port.out;

import org.correos.app.addressvalidation.application.model.AddressToValidate;
import org.correos.app.addressvalidation.infrastructure.google.dto.request.AddressInput;

import java.util.List;

public interface AddressCompletionPort {
    List<String> complete(AddressToValidate address);
}
