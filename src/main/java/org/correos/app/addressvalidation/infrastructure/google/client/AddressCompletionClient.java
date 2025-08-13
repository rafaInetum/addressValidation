package org.correos.app.addressvalidation.infrastructure.google.client;

import org.correos.app.addressvalidation.infrastructure.google.dto.request.AddressInput;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "address-complete", url = "${address.complete.url}")
public interface AddressCompletionClient {

    @PostMapping("/complete-address")
    List<String> execute(@RequestBody AddressInput JsonAddress);
}
