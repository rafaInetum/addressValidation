package org.correos.app.addressvalidation.infrastructure.google.client;

import org.correos.app.addressvalidation.application.model.AddressToValidate;
import org.correos.app.addressvalidation.domain.model.NormalizedAddress;
import org.correos.app.addressvalidation.domain.model.ValidatedAddress;
import org.correos.app.addressvalidation.infrastructure.google.config.GoogleAddressValidationProps;
import org.correos.app.addressvalidation.infrastructure.google.config.GoogleApiProps;
import org.correos.app.addressvalidation.infrastructure.google.dto.request.AddressValidationInputForGoogle;
import org.correos.app.addressvalidation.infrastructure.google.dto.response.GoogleAddressResponse;
import org.correos.app.addressvalidation.infrastructure.google.mapper.GoogleAddressRequestMapper;
import org.correos.app.addressvalidation.infrastructure.google.mapper.response.GoogleAddressResponseMapper;
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
    private final GoogleAddressResponseMapper mapper;
    private final GoogleAddressRequestMapper googleAddressRequestMapper;


    public GoogleAddressValidationClient(RestTemplate googleRestTemplate,
                                         GoogleAddressValidationProps props,
                                         GoogleApiProps googleApiProps,
                                         GoogleAddressResponseMapper mapper,
                                         GoogleAddressRequestMapper googleAddressRequestMapper) {
        this.restTemplate = googleRestTemplate;
        this.googleAddressValidationProps = props;
        this.googleApiProps = googleApiProps;
        this.mapper = mapper;
        this.googleAddressRequestMapper = googleAddressRequestMapper;

    }

    public ValidatedAddress requestValidation(AddressToValidate address, NormalizedAddress normalized) {

        AddressValidationInputForGoogle googleAddress = toGoogleInput(address);

        try {
            ResponseEntity<GoogleAddressResponse> response = restTemplate.exchange(
                    buildUrl(),
                    HttpMethod.POST,
                    buildRequest(googleAddress),
                    GoogleAddressResponse.class
            );

            GoogleAddressResponse body = Optional.ofNullable(response.getBody())
                    .orElseThrow(() -> new RuntimeException("Respuesta vacía de Google Address Validation"));

            return mapToValidatedAddress(body, normalized);

        } catch (RestClientResponseException e) {
            String msg = "Error HTTP llamando a Google Address Validation: status=%d body=%s"
                    .formatted(e.getRawStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException(msg, e);
        } catch (Exception e) {
            throw new RuntimeException("Error al validar dirección con Google", e);
        }
    }

    private AddressValidationInputForGoogle toGoogleInput(AddressToValidate input) {
        return googleAddressRequestMapper.toGoogleInput(input);
    }

    private String buildUrl() {
        return UriComponentsBuilder
                .fromHttpUrl(googleAddressValidationProps.baseUrl())
                .path("/v1:validateAddress")
                .queryParam("key", googleApiProps.apiKey())
                .toUriString();
    }

    private HttpEntity<String> buildRequest(AddressValidationInputForGoogle address) {
        String jsonBody = GoogleAddressRequestBuilder.buildJsonRequest(address);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return new HttpEntity<>(jsonBody, headers);
    }

    private ValidatedAddress mapToValidatedAddress(GoogleAddressResponse response,NormalizedAddress normalized) {
        return mapper.toDomain(response,normalized);
    }
}
