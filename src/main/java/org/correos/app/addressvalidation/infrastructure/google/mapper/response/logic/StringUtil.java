package org.correos.app.addressvalidation.infrastructure.google.mapper.response.logic;

import org.springframework.stereotype.Component;

@Component
public class StringUtil {

    public String prefer(String a, String b) {
        return notBlank(a) ? a : b;
    }

    public boolean notBlank(String s) {
        return s != null && !s.isBlank();
    }

    public String join(String sep, String... parts) {
        String out = null;
        for (String p : parts) {
            if (p == null || p.isBlank()) continue;
            out = (out == null) ? p : out + sep + p;
        }
        return out;
    }
}
