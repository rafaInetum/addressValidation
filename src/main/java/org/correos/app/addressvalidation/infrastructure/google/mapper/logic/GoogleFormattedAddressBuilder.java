package org.correos.app.addressvalidation.infrastructure.google.mapper.logic;

import org.correos.app.addressvalidation.domain.model.NormalizedAddress;
import org.springframework.stereotype.Component;

@Component
public class GoogleFormattedAddressBuilder {

    private final StringUtil stringUtil;

    public GoogleFormattedAddressBuilder(StringUtil stringUtil) {
        this.stringUtil = stringUtil;
    }

    /** Formatea una dirección legible con lo normalizado (idéntico a tu buildFormatted). */
    public String buildFormatted(NormalizedAddress n) {
        return stringUtil.join(", ",
                stringUtil.join(" ", stringUtil.join(" ", n.tipoVia(), n.nombreVia()), n.numero()),
                stringUtil.join(" ", n.codigoPostal(), n.localidad()),
                n.pais()
        );
    }
}
