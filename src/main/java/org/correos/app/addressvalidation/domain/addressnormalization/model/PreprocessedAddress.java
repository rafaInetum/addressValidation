package org.correos.app.addressvalidation.domain.addressnormalization.model;

public record PreprocessedAddress(
        String normalizedText,
        LocaleISO locale,
        Lexicon lexicon
) {}
