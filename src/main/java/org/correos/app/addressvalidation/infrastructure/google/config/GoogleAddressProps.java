package org.correos.app.addressvalidation.infrastructure.google.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "google.address")
public record GoogleAddressProps(
        String baseUrl,
        String apiKey
) {}
