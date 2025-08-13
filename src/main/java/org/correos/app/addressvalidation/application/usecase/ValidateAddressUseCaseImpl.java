package org.correos.app.addressvalidation.application.usecase;

import org.correos.app.addressvalidation.domain.model.ValidatedAddress;
import org.correos.app.addressvalidation.domain.port.out.AddressCompletionPort;
import org.correos.app.addressvalidation.domain.port.out.AddressValidationPort;
import org.correos.app.addressvalidation.domain.port.in.ValidateAddressUseCase;
import org.correos.app.addressvalidation.infrastructure.google.dto.request.AddressInput;
import org.correos.app.addressvalidation.infrastructure.google.dto.response.NextAction;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ValidateAddressUseCaseImpl implements ValidateAddressUseCase {

    private final AddressValidationPort addressValidator;
    private final AddressCompletionPort completionProvider;

    public ValidateAddressUseCaseImpl(AddressValidationPort validator,
                                      AddressCompletionPort completionProvider) {
        this.addressValidator = validator;
        this.completionProvider = completionProvider;
    }

    @Override
    public List<ValidatedAddress> execute(List<AddressInput> addressesInput) {
        return addressesInput.stream()
                .map(this::validate)
                .toList();
    }

    private ValidatedAddress validate(AddressInput addressInput) {
        try {
            validateAddressStructure(addressInput);
            return processValidation(addressInput);
        } catch (Exception e) {
            return ValidatedAddress.error("Error al procesar la dirección: " + e.getMessage());
        }
    }

    private void validateAddressStructure(AddressInput address) {
        boolean invalid = address == null ||
                address.addressLines() == null ||
                address.addressLines().isEmpty() ||
                address.addressLines().get(0).isBlank();

        if (invalid) {
            throw new IllegalArgumentException("El campo 'addressLines' no puede estar vacío");
        }
    }

    private ValidatedAddress processValidation(AddressInput input) {
        ValidatedAddress validated = validateAddress(input);

        if (needsCompletion(validated)) {
            validated = requestCompleteAddressesBySuggestions(validated, input);
        }
        return validated;
    }

    private ValidatedAddress validateAddress(AddressInput input) {
        return addressValidator.requestValidation(input);
    }

    private boolean needsCompletion(ValidatedAddress validated) {return !NextAction.ACCEPT.name().equalsIgnoreCase(validated.nextAction());}

    private ValidatedAddress requestCompleteAddressesBySuggestions(ValidatedAddress validated, AddressInput input) {
        List<String> suggestions = completionProvider.complete(input);
        return validated.withSuggestions(suggestions);
    }

}
