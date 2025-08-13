package org.correos.app.addressvalidation.infrastructure.google.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Address(

        @JsonProperty("formattedAddress")
        String formattedAddress,

        PostalAddress postalAddress,

        List<AddressComponent> addressComponents

) {}
