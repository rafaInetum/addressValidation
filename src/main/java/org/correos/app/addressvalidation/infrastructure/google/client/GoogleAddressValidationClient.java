package org.correos.app.addressvalidation.infrastructure.google.client;

import org.correos.app.addressvalidation.infrastructure.google.config.GoogleAddressProps;
import org.correos.app.addressvalidation.infrastructure.google.dto.request.AddressInput;
import org.correos.app.addressvalidation.infrastructure.google.dto.response.GoogleAddressResponse;
import org.correos.app.addressvalidation.infrastructure.google.util.GoogleAddressRequestBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@Component
public class GoogleAddressValidationClient {

    private final RestTemplate restTemplate;
    private final GoogleAddressProps props;


    public GoogleAddressValidationClient(RestTemplate googleRestTemplate,
                                         GoogleAddressProps props) {
        this.restTemplate = googleRestTemplate;
        this.props = props;
    }

    public GoogleAddressResponse validate(AddressInput address) {
        try {
            ResponseEntity<GoogleAddressResponse> response =
                    restTemplate.exchange(buildUrl(), HttpMethod.POST, buildRequest(address), GoogleAddressResponse.class);

            return Optional.ofNullable(response.getBody())
                    .orElseThrow(() -> new RuntimeException("Respuesta vacía de Google AddressV."));

        } catch (RestClientResponseException e) {
            String msg = "Error HTTP llamando a Google Address Validation: status=%d body=%s"
                    .formatted(e.getRawStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException(msg, e);
        } catch (Exception e) {
            throw new RuntimeException("Error al validar dirección con Google", e);
        }
    }

    private String buildUrl() {
        return UriComponentsBuilder
                .fromHttpUrl(props.baseUrl())
                .path("/v1:validateAddress")
                .queryParam("key", props.apiKey())
                .toUriString();
    }

    private HttpEntity<String> buildRequest(AddressInput address) {
        String jsonBody = GoogleAddressRequestBuilder.buildJsonRequest(address);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return new HttpEntity<>(jsonBody, headers);
    }
}
