package org.correos.app.addressvalidation.domain.model;

public record RawAddress(String rawText, String localeHint, boolean manuallyFixed) {}
