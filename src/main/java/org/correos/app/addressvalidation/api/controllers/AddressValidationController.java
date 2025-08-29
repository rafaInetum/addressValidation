package org.correos.app.addressvalidation.api.controllers;

import org.correos.app.addressvalidation.api.dto.request.AddressValidationRequestDTO;
import org.correos.app.addressvalidation.api.dto.response.ValidatedAddressResponseDTO;
import org.correos.app.addressvalidation.api.mapper.AddressRequestMapper;
import org.correos.app.addressvalidation.api.mapper.AddressResponseMapper;
import org.correos.app.addressvalidation.application.model.AddressValidationInput;
import org.correos.app.addressvalidation.application.port.in.ValidateAddressUseCase;
import org.correos.app.addressvalidation.domain.model.ValidatedAddress;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/addresses")
public class AddressValidationController {

    private final ValidateAddressUseCase validateAddressUseCase;
    private final AddressRequestMapper addressRequestMapper;
    private final AddressResponseMapper addressResponseMapper;


    public AddressValidationController(
            ValidateAddressUseCase validateAddressUseCase,
            AddressRequestMapper addressRequestMapper,
            AddressResponseMapper validatedAddressMapper
    ) {
        this.validateAddressUseCase = validateAddressUseCase;
        this.addressRequestMapper = addressRequestMapper;
        this.addressResponseMapper = validatedAddressMapper;
    }

    /**
     * POST /v1/addresses/validation
     * Valida un LOTE de direcciones.
     *
     * Body: JSON con una lista de AddressValidationRequestDTO.
     * Devuelve: 200 OK con List<ValidatedAddressResponseDTO>.
     *
     */
    @PostMapping("/validation")
    public ResponseEntity<?> addressValidation(@RequestBody List<AddressValidationRequestDTO> addresses) {
        List<AddressValidationInput> toValidate = addressRequestMapper.toModelList(addresses);
        List<ValidatedAddress> results = validateAddressUseCase.execute(toValidate);
        return ResponseEntity.ok(addressResponseMapper.tolistDTO(results));
    }

    /**
     * POST /v1/addresses/completion
     * Valida/normaliza UNA única dirección.
     *
     * Body: JSON con un AddressValidationRequestDTO.
     * Devuelve: 200 OK con ValidatedAddressResponseDTO.
     *
     */
    @PostMapping("/completion")
    public ResponseEntity<?> addressCompletion(@RequestBody AddressValidationRequestDTO address) {
        AddressValidationInput toValidate = addressRequestMapper.toModel(address);
        ValidatedAddress results = validateAddressUseCase.execute(toValidate);
        return ResponseEntity.ok(addressResponseMapper.toDTO(results));
    }

    /**
     * Recurso concreto por ID usando PathParam.
     * Ejemplo: GET /v1/addresses/123456
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getAddressById(@PathVariable("id") Long idDireccion) {
        // TODO: implementar con un caso de uso tipo GetAddressByIdUseCase
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(Map.of(
                        "status", "NOT_IMPLEMENTED",
                        "message", "Pending implementation: fetch single address by ID",
                        "id", idDireccion
                ));
    }

    /**
     * Geolocalización (Point) del recurso concreto.
     * Regla: PathParam para el recurso concreto + sub-recurso "geolocation".
     * Ejemplo: GET /v1/addresses/12345/geolocation
     */
    @GetMapping("/geolocation/{id}")
    public ResponseEntity<?> getAddressGeoLocation(@PathVariable("id") Long idDireccion) {
        // TODO: implementar usando un use case tipo GetGeoLocationUseCase + repository sobre tb_sirius_direccion
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(Map.of(
                        "status", "NOT_IMPLEMENTED",
                        "message", "Pending implementation: fetch geometry(Point,4326) from normalized DB",
                        "id", idDireccion
                ));
    }

    /**
     * Validación de direcciones desde fichero (CSV/XLSX/JSON).
     * Sustantivo en inglés y plural según tu criterio → "fileValidations".
     * Ejemplo: POST /v1/addresses/fileValidations (multipart)
     * Opcional: QueryParam para hints/flags del proceso (locale, async, etc.).
     */
    @PostMapping(
            value = "/fileValidations",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> validateFromFile(
            @RequestPart("file") MultipartFile file,
            @RequestParam(name = "locale", required = false) String localeHint,
            @RequestParam(name = "async", defaultValue = "false") boolean async
    ) {
        // TODO: parse del fichero, mapear a AddressValidationInput y procesar en lote
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(Map.of(
                        "status", "NOT_IMPLEMENTED",
                        "message", "Pending implementation: validation from uploaded file",
                        "fileName", file.getOriginalFilename(),
                        "locale", localeHint,
                        "async", async
                ));
    }
}
