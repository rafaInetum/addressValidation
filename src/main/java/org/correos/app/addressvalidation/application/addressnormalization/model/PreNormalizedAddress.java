package org.correos.app.addressvalidation.application.addressnormalization.model;

public record PreNormalizedAddress(
        String normalizedText,
        LocaleISO locale,
        Lexicon lexicon,
        String formattedAddress,
        String streetName,
        String streetNumber,
        String addressComplements,
        String postalCode,
        String locality,
        String province,
        String country
) {
    public static PreNormalizedAddress empty(LocaleISO locale, Lexicon lexicon) {
        return new PreNormalizedAddress(
                "",     // normalizedText
                locale, // locale
                lexicon,// lexicon
                null,   // formattedAddress
                null,   // streetName
                null,   // streetNumber
                null,   // addressComplements
                null,   // postalCode
                null,   // locality
                null,   // province
                null    // country
        );
    }
}
