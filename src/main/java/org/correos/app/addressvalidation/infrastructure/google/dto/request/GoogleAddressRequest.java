package org.correos.app.addressvalidation.infrastructure.google.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record GoogleAddressRequest(@JsonProperty("address") List<AddressInput> address){}
