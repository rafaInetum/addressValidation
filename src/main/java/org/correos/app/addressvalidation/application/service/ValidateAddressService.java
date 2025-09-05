package org.correos.app.addressvalidation.application.service;

import lombok.RequiredArgsConstructor;
import org.correos.app.addressvalidation.application.addressnormalization.rule.GeocodeReliabilityAdjuster;
import org.correos.app.addressvalidation.application.addressnormalization.service.AddressNormalizer;
import org.correos.app.addressvalidation.application.mapper.AddressToValidateBuilder;
import org.correos.app.addressvalidation.application.model.AddressToValidate;
import org.correos.app.addressvalidation.application.model.AddressValidationInput;
import org.correos.app.addressvalidation.application.port.in.ValidateAddressUseCase;
import org.correos.app.addressvalidation.application.port.out.AddressValidationPort;
import org.correos.app.addressvalidation.domain.model.NextAction;
import org.correos.app.addressvalidation.domain.model.NormalizedAddress;
import org.correos.app.addressvalidation.domain.model.ValidatedAddress;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ValidateAddressService implements ValidateAddressUseCase {


    private final AddressNormalizer normalizer;
    private final AddressValidationPort addressValidator;
    private final CompleteAddressService completionProvider;
    private final GeocodeReliabilityAdjuster geocodeAdjuster;
    private final AddressToValidateBuilder normalizedMapper;

/* =========================
       Main methods
       ========================= */

    @Override
    public List<ValidatedAddress> validate(List<AddressValidationInput> input) {
        if (input == null || input.isEmpty()) return List.of();
        return input.stream().map(this::processAddress).toList();
    }

    @Override
    public ValidatedAddress processAddress(AddressValidationInput input) {
        if (isBlank(input)) {
            return ValidatedAddress.error("El campo 'originalAddress' no puede estar vacío");
        }
        try {

            // 1) Construye el AddressToValidate
            AddressToValidate toValidate = normalizedMapper.buildAddressToValidate(input);

            // 2) Validar con proveedor
            ValidatedAddress validated = addressValidator.requestValidation(toValidate);

            // 3) Ajustar fiabilidad de geocodificación
            validated = geocodeAdjuster.adjust(validated, Boolean.TRUE.equals(input.manuallyFixed()));

            // 4) Normalizar
            NormalizedAddress normalized = normalizer.normalize(validated);

            // 5) Añadir sugerencias si no es ACCEPT
            return requiresCompletion(validated)
                    ? validated.withSuggestions(safeSuggestions(toValidate))
                    : validated;

        } catch (Exception e) {
            return ValidatedAddress.error("Error al procesar la dirección: " + e.getMessage());
        }
    }

    private boolean requiresCompletion(ValidatedAddress v) {
        return v != null && v.nextAction() != NextAction.ACCEPT;
    }

    private List<String> safeSuggestions(AddressToValidate toValidate) {
        try {
            List<String> s = completionProvider.execute(toValidate);
            return (s == null) ? List.of() : s;
        } catch (Exception ignored) {
            return List.of();
        }
    }

    /* =========================
       Helpers methods
       ========================= */

    private boolean isBlank(AddressValidationInput input) {
        return input == null || input.originalAddress() == null || input.originalAddress().isBlank();
    }

}
