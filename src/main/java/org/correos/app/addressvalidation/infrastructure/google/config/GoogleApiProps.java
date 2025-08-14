package org.correos.app.addressvalidation.infrastructure.google.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "google")
public record GoogleApiProps(String apiKey) {
}
