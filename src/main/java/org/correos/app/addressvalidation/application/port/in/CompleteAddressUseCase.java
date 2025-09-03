package org.correos.app.addressvalidation.application.port.in;

import org.correos.app.addressvalidation.application.model.AddressToValidate;

import java.util.List;

public interface CompleteAddressUseCase {
    List<String> execute(AddressToValidate address);
    List<String> execute(String address);
}
