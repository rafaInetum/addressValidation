package org.correos.app.addressvalidation.application.port.in;

import org.correos.app.addressvalidation.application.model.RawAddressToValidate;
import org.correos.app.addressvalidation.domain.model.ValidatedAddress;

import java.util.List;

public interface ValidateAddressUseCase {
    List<ValidatedAddress> execute(List<RawAddressToValidate> addresses);
}
