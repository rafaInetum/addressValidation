package org.correos.app.addressvalidation.application.mapper;

import org.correos.app.addressvalidation.application.model.AddressToValidate;
import org.correos.app.addressvalidation.domain.model.NormalizedAddress;

import java.util.List;

public class NormalizedAddressToValidateMapper {

        public static AddressToValidate toStructuredAddress(NormalizedAddress n) {
            if (n == null) return null;

            return new AddressToValidate(
                    normalizeRegionCode(n.pais()),      // regionCode
                    n.localidad(),                      // locality
                    n.codigoPostal(),                   // postalCode
                    buildAddressLines(n)                // addressLines
            );
        }

        private static String normalizeRegionCode(String pais) {
            if (pais == null) return null;
            return switch (pais.toUpperCase()) {
                case "ESPAÑA" -> "ES";
                case "PORTUGAL" -> "PT";
                case "ANDORRA" -> "AD";
                default -> pais.substring(0, Math.min(2, pais.length())).toUpperCase();
            };
        }

        private static List<String> buildAddressLines(NormalizedAddress n) {
            StringBuilder sb = new StringBuilder();
            if (n.tipoVia() != null) sb.append(n.tipoVia()).append(" ");
            if (n.nombreVia() != null) sb.append(n.nombreVia()).append(" ");
            if (n.numero() != null) sb.append(n.numero()).append(" ");
            if (n.planta() != null) sb.append("PLANTA ").append(n.planta()).append(" ");
            if (n.puerta() != null) sb.append("PUERTA ").append(n.puerta());

            return List.of(sb.toString().trim());
        }




}
