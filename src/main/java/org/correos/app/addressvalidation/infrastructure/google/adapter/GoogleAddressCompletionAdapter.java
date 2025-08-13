package org.correos.app.addressvalidation.infrastructure.google.adapter;

import lombok.extern.slf4j.Slf4j;
import org.correos.app.addressvalidation.domain.port.out.AddressCompletionPort;
import org.correos.app.addressvalidation.infrastructure.google.client.AddressCompletionClient;
import org.correos.app.addressvalidation.infrastructure.google.dto.request.AddressInput;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class GoogleAddressCompletionAdapter implements AddressCompletionPort {

    private final AddressCompletionClient client;

    public GoogleAddressCompletionAdapter(AddressCompletionClient client) {
        this.client = client;
    }

    @Override
    public List<String> complete(AddressInput address) {
        try {
            return client.execute(address);
        } catch (Exception e) {
            log.error("Fallo al completar dirección", e);
            return List.of();
        }
    }
}
