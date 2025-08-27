package org.correos.app.addressvalidation.infrastructure.google.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.correos.app.addressvalidation.infrastructure.google.config.GoogleApiProps;
import org.correos.app.addressvalidation.infrastructure.google.config.GooglePlaceDetailsProps;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class GooglePlaceDetailsClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final GooglePlaceDetailsProps googlePlaceDetailsProps;
    private final GoogleApiProps googleApiProps;

    public GooglePlaceDetailsClient(
            RestTemplate restTemplate,
            ObjectMapper mapper,
            GooglePlaceDetailsProps googlePlaceDetailsProps,
            GoogleApiProps googleApiProps) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.googlePlaceDetailsProps = googlePlaceDetailsProps;
        this.googleApiProps = googleApiProps;
    }

    public List<String> fetchFormattedAddresses(List<String> placeIds) {
        return placeIds.stream()
                .map(this::fetchFormattedAddresses)
                .filter(Objects::nonNull)
                .toList();
    }

    private String fetchFormattedAddresses(String placeId) {
        try {
            String url = buildUrl(placeId);

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JsonNode result = mapper.readTree(response.getBody()).path("result");

            return result.path("formatted_address").asText();

        } catch (RestClientResponseException e) {
            log.error("Error HTTP al consultar Place Details para {}. status={} body={}", placeId,
                    e.getRawStatusCode(), e.getResponseBodyAsString(), e);
            return null;
        } catch (Exception e) {
            log.error("Error al procesar Place Details para {}", placeId, e);
            return null;
        }
    }

    private String buildUrl(String placeId) {
        return UriComponentsBuilder
                .fromHttpUrl(googlePlaceDetailsProps.baseUrl())
                .queryParam("place_id", placeId)
                .queryParam("fields", "formatted_address")
                .queryParam("language", "es")
                .queryParam("key", googleApiProps.apiKey())
                .toUriString();
    }
}
