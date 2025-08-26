package org.correos.app.addressvalidation.application.addressnormalization.model;

public record PreprocessedAddress(
        String normalizedText,
        LocaleISO locale,
        Lexicon lexicon
) {}
