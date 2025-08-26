package org.correos.app.addressvalidation.application.addressnormalization.model;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public record Lexicon(
        Map<String, String> abbr,
        Set<String> tipos,
        Pattern cpPattern
) {}
