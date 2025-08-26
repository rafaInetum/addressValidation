package org.correos.app.addressvalidation.application.service;

import org.correos.app.addressvalidation.application.mapper.NormalizedAddressToValidateMapper;
import org.correos.app.addressvalidation.application.model.AddressToValidate;
import org.correos.app.addressvalidation.application.model.RawAddressToValidate;
import org.correos.app.addressvalidation.application.port.in.ValidateAddressUseCase;
import org.correos.app.addressvalidation.application.port.out.AddressCompletionPort;
import org.correos.app.addressvalidation.application.port.out.AddressValidationPort;
import org.correos.app.addressvalidation.domain.model.NextAction;
import org.correos.app.addressvalidation.domain.model.NormalizedAddress;
import org.correos.app.addressvalidation.domain.model.RawAddress;
import org.correos.app.addressvalidation.domain.model.ValidatedAddress;
import org.correos.app.addressvalidation.domain.addressnormalization.service.AddressNormalizer;
import org.correos.app.addressvalidation.domain.addressnormalization.rule.GeocodeReliabilityAdjuster;
import static org.correos.app.addressvalidation.application.mapper.RawAddressMapper.toDomain;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ValidatedAddressService implements ValidateAddressUseCase {

    private final AddressNormalizer normalizer;
    private final AddressValidationPort addressValidator;
    private final AddressCompletionPort completionProvider;
    private final GeocodeReliabilityAdjuster geocodeAdjuster;


    public ValidatedAddressService(AddressNormalizer normalizer,
                                   AddressValidationPort addressValidator,
                                   AddressCompletionPort completionProvider,
                                   GeocodeReliabilityAdjuster geocodeAdjuster) {
        this.normalizer = normalizer;
        this.addressValidator = addressValidator;
        this.completionProvider = completionProvider;
        this.geocodeAdjuster = geocodeAdjuster;
    }

    @Override
    public List<ValidatedAddress> execute(List<RawAddressToValidate> input) {
        return input.stream()
                .map(this::validate)
                .toList();
    }

    private ValidatedAddress validate(RawAddressToValidate rawAddress) {

        try {
            validateAddressStructure(rawAddress);

            // Paso 1: normalizar
            RawAddress rawAddressDomain = toDomain(rawAddress);
            NormalizedAddress normalized = normalizer.normalize(rawAddressDomain);
//
            // Paso 2: convertir a AddressToValidate estructurado
            AddressToValidate structured = NormalizedAddressToValidateMapper.toStructuredAddress(normalized);

            // Paso 3: validar usando proveedor (Google u otro)
            ValidatedAddress validated = addressValidator.requestValidation(structured);

            // Paso 4: ajustar fiabilidad de geocodificación
            validated = geocodeAdjuster.adjust(validated, rawAddress.manuallyFixed());

            // Paso 5: si no es ACCEPT, busca sugerencias
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
