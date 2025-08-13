package org.correos.app.addressvalidation.domain.port.out;

import org.correos.app.addressvalidation.infrastructure.google.dto.request.AddressInput;

import java.util.List;

public interface AddressCompletionPort {
    List<String> complete(AddressInput address);
}
