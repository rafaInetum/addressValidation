package org.correos.app.addressvalidation.domain.addressnormalization.utils;

import org.correos.app.addressvalidation.domain.addressnormalization.model.LocaleISO;

import java.util.regex.Pattern;

public class AddressUtils {

    public static String matchFirst(Pattern pattern, String s) {
        var matcher = pattern.matcher(s);
        return matcher.find() ? matcher.group() : null;
    }

    public static String detectCountry(String s, LocaleISO guess) {
        String u = s.toUpperCase();
        if (u.contains("PORTUGAL")) return "PORTUGAL";
        if (u.contains("ANDORRA")) return "ANDORRA";
        if (u.contains("ESPAÑA") || u.contains("SPAIN") || guess == LocaleISO.ES) return "ESPAÑA";
        return null;
    }

    private AddressUtils() {
    }
}
