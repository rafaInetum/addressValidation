package org.correos.app.addressvalidation.application.service;

import org.correos.app.addressvalidation.application.addressnormalization.rule.GeocodeReliabilityAdjuster;
import org.correos.app.addressvalidation.application.addressnormalization.service.AddressNormalizer;
import org.correos.app.addressvalidation.application.mapper.NormalizedAddressToValidateMapper;
import org.correos.app.addressvalidation.application.mapper.RawAddressMapper;
import org.correos.app.addressvalidation.application.model.AddressToValidate;
import org.correos.app.addressvalidation.application.model.RawAddressToValidate;
import org.correos.app.addressvalidation.application.port.in.ValidateAddressUseCase;
import org.correos.app.addressvalidation.application.port.out.AddressValidationPort;
import org.correos.app.addressvalidation.domain.model.NextAction;
import org.correos.app.addressvalidation.domain.model.NormalizedAddress;
import org.correos.app.addressvalidation.domain.model.RawAddress;
import org.correos.app.addressvalidation.domain.model.ValidatedAddress;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class ValidatedAddressService implements ValidateAddressUseCase {

    private final AddressNormalizer normalizer;
    private final AddressValidationPort addressValidator;
    private final CompletedAddressService completionProvider;
    private final GeocodeReliabilityAdjuster geocodeAdjuster;
    private final RawAddressMapper rawAddressMapper;
    private final NormalizedAddressToValidateMapper normalizedMapper;


    public ValidatedAddressService(AddressNormalizer normalizer,
                                   AddressValidationPort addressValidator,
                                   CompletedAddressService completionProvider,
                                   GeocodeReliabilityAdjuster geocodeAdjuster,
                                   RawAddressMapper rawAddressMapper,
                                   NormalizedAddressToValidateMapper normalizedMapper) {
        this.normalizer = normalizer;
        this.addressValidator = addressValidator;
        this.completionProvider = completionProvider;
        this.geocodeAdjuster = geocodeAdjuster;
        this.rawAddressMapper = rawAddressMapper;
        this.normalizedMapper = normalizedMapper;
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
            RawAddress rawAddressDomain = rawAddressMapper.toDomain(rawAddress);

            NormalizedAddress normalized = normalizer.normalize(rawAddressDomain);

            AddressToValidate atv = normalizedMapper.toStructuredAddress(normalized);

            // Paso 2: convertir a AddressToValidate estructurado
            atv = applyFallbackIfNeeded(atv,rawAddress);

            // Paso 3: validar usando proveedor (Google u otro)
            ValidatedAddress validated = addressValidator.requestValidation(atv, normalized);

            // Paso 4: ajustar fiabilidad de geocodificación
            validated = geocodeAdjuster.adjust(validated, rawAddress.manuallyFixed());

            // Paso 5: si no es ACCEPT, busca sugerencias
            if (needsCompletion(validated)) {
                List<String> suggestions = completionProvider.execute(atv);
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

    private AddressToValidate applyFallbackIfNeeded(AddressToValidate structured, RawAddressToValidate raw) {
        if (/*isFallbackRequired(structured)*/true) {
            return new AddressToValidate(
                    raw.localeHint() != null ? raw.localeHint() : "ES",
                    null,
                    null,
                    List.of(raw.rawText().trim())
            );
        }
        return structured;
    }

    private boolean isFallbackRequired(AddressToValidate addr) {
        return (addr.regionCode() == null || addr.regionCode().isBlank()) &&
                (addr.city() == null || addr.city().isBlank()) &&
                (addr.postalCode() == null || addr.postalCode().isBlank());
    }



}
