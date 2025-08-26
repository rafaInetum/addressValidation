package org.correos.app.addressvalidation.infrastructure.google.mapper.logic;

import org.correos.app.addressvalidation.infrastructure.google.dto.response.Result;
import org.springframework.stereotype.Component;

@Component
public class GoogleComponentReader {

    /**
     * Extrae componentName.text para los tipos indicados; si falta, intenta postalAddress.
     * Misma lógica que tu método comp(...)
     */
    public String findComponentText(Result r, String... types) {
        if (r == null || r.address() == null) return null;

        var comps = r.address().addressComponents();
        if (comps != null) {
            for (var c : comps) {
                String t = c.componentType() == null ? "" : c.componentType().toUpperCase();
                for (String wanted : types) {
                    String W = wanted.toUpperCase();
                    if (t.equals(W) || t.contains(W)) {
                        return c.componentName() != null ? c.componentName().text() : null;
                    }
                }
            }
        }
        var pa = r.address().postalAddress();
        if (pa != null && types.length == 1) {
            String w = types[0].toUpperCase();
            if ("POSTAL_CODE".equals(w))  return pa.postalCode();
            if ("LOCALITY".equals(w) || "POSTAL_TOWN".equals(w)) return pa.locality();
            if ("COUNTRY".equals(w))      return pa.regionCode();
        }
        return null;
    }
}
