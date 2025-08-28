package org.correos.app.addressvalidation.application.service;

import org.correos.app.addressvalidation.application.addressnormalization.rule.GeocodeReliabilityAdjuster;
import org.correos.app.addressvalidation.application.addressnormalization.service.AddressNormalizer;
import org.correos.app.addressvalidation.application.mapper.NormalizedAddressToValidateMapper;
import org.correos.app.addressvalidation.application.model.AddressToValidate;
import org.correos.app.addressvalidation.application.model.AddressValidationInput;
import org.correos.app.addressvalidation.application.port.in.ValidateAddressUseCase;
import org.correos.app.addressvalidation.application.port.out.AddressValidationPort;
import org.correos.app.addressvalidation.domain.model.NextAction;
import org.correos.app.addressvalidation.domain.model.NormalizedAddress;
import org.correos.app.addressvalidation.domain.model.ValidatedAddress;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class ValidatedAddressService implements ValidateAddressUseCase {

    private final AddressNormalizer normalizer;
    private final AddressValidationPort addressValidator;
    private final CompletedAddressService completionProvider;
    private final GeocodeReliabilityAdjuster geocodeAdjuster;
    private final NormalizedAddressToValidateMapper normalizedMapper;


    public ValidatedAddressService(AddressNormalizer normalizer,
                                   AddressValidationPort addressValidator,
                                   CompletedAddressService completionProvider,
                                   GeocodeReliabilityAdjuster geocodeAdjuster,
                                   NormalizedAddressToValidateMapper normalizedMapper) {
        this.normalizer = normalizer;
        this.addressValidator = addressValidator;
        this.completionProvider = completionProvider;
        this.geocodeAdjuster = geocodeAdjuster;
        this.normalizedMapper = normalizedMapper;
    }

    @Override
    public List<ValidatedAddress> execute(List<AddressValidationInput> input) {
        return input.stream()
                .map(this::validate)
                .toList();
    }

    private ValidatedAddress validate(AddressValidationInput input) {
        try {

            validateAddressStructure(input);

            /* Paso 1: normalizar */
            NormalizedAddress normalized = normalizer.normalize(input);

            /* Paso 2: convertir a AddressToValidate estructurado */
            AddressToValidate atv = normalizedMapper.toStructuredAddress(normalized);
            atv = applyFallbackIfNeeded(atv,input); // En caso de llegar sin estructura, usar texto plano

            /* Paso 3: validar usando proveedor (Google u otro) */
            ValidatedAddress validated = addressValidator.requestValidation(atv, normalized);

            /* Paso 4: ajustar fiabilidad de geocodificación */
            validated = geocodeAdjuster.adjust(validated, input.manuallyFixed());

            /* Paso 5: si no es ACCEPT, busca sugerencias */
            if (needsCompletion(validated)) {
                List<String> suggestions = completionProvider.execute(atv);
                validated = validated.withSuggestions(suggestions);
            }
            return validated;

        } catch (Exception e) {
            return ValidatedAddress.error("Error al procesar la dirección: " + e.getMessage());
        }
    }

    private void validateAddressStructure(AddressValidationInput address) {
        boolean invalid = address == null ||
                address.addressPlainText() == null ||
                address.addressPlainText().isBlank();

        if (invalid) {
            throw new IllegalArgumentException("El campo 'addressPlainText' no puede estar vacío");
        }
    }

    private AddressToValidate applyFallbackIfNeeded(AddressToValidate structured, AddressValidationInput raw) {
        if (isFallbackRequired(structured)) {
            return new AddressToValidate(
                    raw.localeHint() != null ? raw.localeHint() : "ES",
                    null,
                    null,
                    List.of(raw.addressPlainText().trim())
            );
        }
        return structured;
    }

    private boolean isFallbackRequired(AddressToValidate addr) {
        return (addr.regionCode() == null || addr.regionCode().isBlank()) &&
                (addr.locality() == null || addr.locality().isBlank()) &&
                (addr.postalCode() == null || addr.postalCode().isBlank());
    }

    private boolean needsCompletion(ValidatedAddress validated) {
        return validated.nextAction() != NextAction.ACCEPT;
    }
}
