package org.correos.app.addressvalidation.infrastructure.google.client;

import org.correos.app.addressvalidation.application.model.AddressToValidate;
import org.correos.app.addressvalidation.domain.model.ValidatedAddress;
import org.correos.app.addressvalidation.infrastructure.google.config.GoogleAddressValidationProps;
import org.correos.app.addressvalidation.infrastructure.google.config.GoogleApiProps;
import org.correos.app.addressvalidation.infrastructure.google.dto.request.AddressInput;
import org.correos.app.addressvalidation.infrastructure.google.dto.response.GoogleAddressResponse;
import org.correos.app.addressvalidation.infrastructure.google.mapper.GoogleAddressMapper;
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
    private final GoogleAddressValidationProps googleAddressValidationProps;
    private final GoogleApiProps googleApiProps;
    private final GoogleAddressMapper mapper;

    public GoogleAddressValidationClient(RestTemplate googleRestTemplate,
                                         GoogleAddressValidationProps props,
                                         GoogleApiProps googleApiProps,
                                         GoogleAddressMapper mapper) {
        this.restTemplate = googleRestTemplate;
        this.googleAddressValidationProps = props;
        this.googleApiProps = googleApiProps;
        this.mapper = mapper;
    }

    public ValidatedAddress requestValidation(AddressToValidate address) {

        AddressInput googleAddress = toGoogleInput(address);

        try {
            ResponseEntity<GoogleAddressResponse> response = restTemplate.exchange(
                    buildUrl(),
                    HttpMethod.POST,
                    buildRequest(googleAddress),
                    GoogleAddressResponse.class
            );

            GoogleAddressResponse body = Optional.ofNullable(response.getBody())
                    .orElseThrow(() -> new RuntimeException("Respuesta vacía de Google Address Validation"));

            return mapToValidatedAddress(body);

        } catch (RestClientResponseException e) {
            String msg = "Error HTTP llamando a Google Address Validation: status=%d body=%s"
                    .formatted(e.getRawStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException(msg, e);
        } catch (Exception e) {
            throw new RuntimeException("Error al validar dirección con Google", e);
        }
    }

    private AddressInput toGoogleInput(AddressToValidate input) {
        return new AddressInput(
                input.regionCode(),
                input.locality(),
                input.postalCode(),
                input.addressLines()
        );
    }

    private String buildUrl() {
        return UriComponentsBuilder
                .fromHttpUrl(googleAddressValidationProps.baseUrl())
                .path("/v1:validateAddress")
                .queryParam("key", googleApiProps.apiKey())
                .toUriString();
    }

    private HttpEntity<String> buildRequest(AddressInput address) {
        String jsonBody = GoogleAddressRequestBuilder.buildJsonRequest(address);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return new HttpEntity<>(jsonBody, headers);
    }

    private ValidatedAddress mapToValidatedAddress(GoogleAddressResponse response) {
        return mapper.toDomain(response);
    }
}
