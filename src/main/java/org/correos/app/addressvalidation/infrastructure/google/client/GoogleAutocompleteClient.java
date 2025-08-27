package org.correos.app.addressvalidation.infrastructure.google.client;


import org.correos.app.addressvalidation.application.model.AddressToValidate;
import org.correos.app.addressvalidation.infrastructure.google.config.GoogleApiProps;
import org.correos.app.addressvalidation.infrastructure.google.config.GoogleAutocompletePlacesProps;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.IntStream;

@Component
public class GoogleAutocompleteClient {

    private final RestTemplate restTemplate;
    private final GoogleAutocompletePlacesProps autoCompleteApiProps;
    private final GoogleApiProps googleApiProps;

    public GoogleAutocompleteClient(
            RestTemplate restTemplate,
            GoogleAutocompletePlacesProps autoCompleteApiProps,
            GoogleApiProps googleApiProps) {
        this.restTemplate = restTemplate;
        this.autoCompleteApiProps = autoCompleteApiProps;
        this.googleApiProps = googleApiProps;
    }

    public List<String> searchPlaceIds(AddressToValidate address) {
        String rawInput = String.join(" ", address.addressLines()) +
                " " + address.city() +
                " " + address.postalCode();
        return searchPlaceIds(rawInput);
    }

    public List<String> searchPlaceIds(String addressText) {
        try {
            URI uri = UriComponentsBuilder.fromHttpUrl(autoCompleteApiProps.baseUrl())
                    .queryParam("input", addressText)
                    .queryParam("types", "address")
                    .queryParam("language", "es")
                    .queryParam("components", "country:es")
                    .queryParam("key", googleApiProps.apiKey())
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUri();

            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
            JSONArray predictions = new JSONObject(response.getBody()).getJSONArray("predictions");

            return IntStream.range(0, predictions.length())
                    .mapToObj(i -> predictions.getJSONObject(i).getString("place_id"))
                    .toList();

        } catch (Exception e) {
            throw new RuntimeException("Error al obtener placeIds : " + e.getMessage(), e);
        }
    }
}
