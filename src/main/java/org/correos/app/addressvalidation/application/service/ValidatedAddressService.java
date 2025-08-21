package org.correos.app.addressvalidation.application.service;

import org.correos.app.addressvalidation.application.mapper.NormalizedAddressToValidateMapper;
import org.correos.app.addressvalidation.application.model.AddressToValidate;
import org.correos.app.addressvalidation.application.model.RawAddressToValidate;
import org.correos.app.addressvalidation.application.port.in.ValidateAddressUseCase;
import org.correos.app.addressvalidation.application.port.out.AddressCompletionPort;
import org.correos.app.addressvalidation.application.port.out.AddressValidationPort;
import org.correos.app.addressvalidation.domain.model.NextAction;
import org.correos.app.addressvalidation.domain.model.NormalizedAddress;
import org.correos.app.addressvalidation.domain.model.ValidatedAddress;
import org.correos.app.addressvalidation.domain.service.AddressNormalizerService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ValidatedAddressService implements ValidateAddressUseCase {

    private final AddressNormalizerService normalizer;
    private final AddressValidationPort addressValidator;
    private final AddressCompletionPort completionProvider;

    public ValidatedAddressService(AddressNormalizerService normalizer,
                                   AddressValidationPort addressValidator,
                                   AddressCompletionPort completionProvider) {
        this.normalizer = normalizer;
        this.addressValidator = addressValidator;
        this.completionProvider = completionProvider;
    }

    @Override
    public List<ValidatedAddress> execute(List<RawAddressToValidate> input) {
        return input.stream()
                .map(this::validate)
                .toList();
    }

    private ValidatedAddress validate(RawAddressToValidate addressRaw) {

        try {
            validateAddressStructure(addressRaw);

            // Paso 1: normalizar
            NormalizedAddress normalized = normalizer.normalize(addressRaw);

            // Paso 2: convertir a AddressToValidate estructurado
            AddressToValidate structured = NormalizedAddressToValidateMapper.toStructuredAddress(normalized);

            // Paso 3: validar usando proveedor (Google u otro)
            ValidatedAddress validated = addressValidator.requestValidation(structured);

            // Paso 4: si no es ACCEPT, hacer sugerencias
            if (needsCompletion(validated)) {
                List<String> suggestions = completionProvider.complete(structured);
                validated = validated.withSuggestions(suggestions);
            }
            return validated;

        } catch (Exception e) {
            return ValidatedAddress.error("Error al procesar la dirección: " + e.getMessage());
        }
    }

    private void validateAddressStructure(RawAddressToValidate address) {
        boolean invalid = address == null ||
                address.rawText() == null ||
                address.rawText().isBlank();

        if (invalid) {
            throw new IllegalArgumentException("El campo 'rawText' no puede estar vacío");
        }
    }

    private boolean needsCompletion(ValidatedAddress validated) {
        return validated.nextAction() != NextAction.ACCEPT;
    }
}
