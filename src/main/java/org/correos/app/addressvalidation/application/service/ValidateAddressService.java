package org.correos.app.addressvalidation.application.service;

import org.correos.app.addressvalidation.application.model.AddressToValidate;
import org.correos.app.addressvalidation.domain.model.ValidatedAddress;
import org.correos.app.addressvalidation.application.port.out.AddressCompletionPort;
import org.correos.app.addressvalidation.application.port.out.AddressValidationPort;
import org.correos.app.addressvalidation.application.port.in.ValidateAddressUseCase;
import org.correos.app.addressvalidation.infrastructure.google.dto.request.AddressInput;
import org.correos.app.addressvalidation.infrastructure.google.dto.response.NextAction;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ValidateAddressService implements ValidateAddressUseCase {

    private final AddressValidationPort addressValidator;
    private final AddressCompletionPort completionProvider;

    public ValidateAddressService(AddressValidationPort validator,
                                  AddressCompletionPort completionProvider) {
        this.addressValidator = validator;
        this.completionProvider = completionProvider;
    }

    @Override
    public List<ValidatedAddress> execute(List<AddressToValidate> addresses) {
        return addresses.stream()
                .map(this::validate)
                .toList();
    }

    private ValidatedAddress validate(AddressToValidate address) {
        try {
            validateAddressStructure(address);
            return processValidation(address);
        } catch (Exception e) {
            return ValidatedAddress.error("Error al procesar la dirección: " + e.getMessage());
        }
    }

    private void validateAddressStructure(AddressToValidate address) {
        boolean invalid = address == null ||
                address.addressLines() == null ||
                address.addressLines().isEmpty() ||
                address.addressLines().get(0).isBlank();

        if (invalid) {
            throw new IllegalArgumentException("El campo 'addressLines' no puede estar vacío");
        }
    }

    private ValidatedAddress processValidation(AddressToValidate address) {
        ValidatedAddress validated = validateAddress(address);

        if (needsCompletion(validated)) {
            validated = requestCompleteAddressesBySuggestions(validated, address);
        }
        return validated;
    }

    private ValidatedAddress validateAddress(AddressToValidate address) {
        return addressValidator.requestValidation(address);
    }

    private boolean needsCompletion(ValidatedAddress validated) {return !NextAction.ACCEPT.name().equalsIgnoreCase(validated.nextAction());}

    private ValidatedAddress requestCompleteAddressesBySuggestions(ValidatedAddress validated, AddressToValidate address) {
        List<String> suggestions = completionProvider.complete(address);
        return validated.withSuggestions(suggestions);
    }

}
