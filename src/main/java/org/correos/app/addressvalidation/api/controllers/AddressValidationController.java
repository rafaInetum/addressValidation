package org.correos.app.addressvalidation.api.controllers;

import lombok.RequiredArgsConstructor;
import org.correos.app.addressvalidation.api.dto.request.AddressValidationRequestDTO;
import org.correos.app.addressvalidation.api.mapper.AddressRequestMapper;
import org.correos.app.addressvalidation.api.mapper.AddressResponseMapper;
import org.correos.app.addressvalidation.application.model.AddressValidationInput;
import org.correos.app.addressvalidation.application.port.in.CompleteAddressUseCase;
import org.correos.app.addressvalidation.application.port.in.ValidateAddressUseCase;
import org.correos.app.addressvalidation.domain.model.ValidatedAddress;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/addresses")
public class AddressValidationController {

    private final ValidateAddressUseCase validateAddressUseCase;
    private final CompleteAddressUseCase completeAddressUseCase;
    private final AddressRequestMapper addressRequestMapper;
    private final AddressResponseMapper addressResponseMapper;

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
        List<ValidatedAddress> results = validateAddressUseCase.validate(toValidate);
        return ResponseEntity.ok(addressResponseMapper.tolistDTO(results));
    }

    /**
     * Entrega direcciones sugeridas apartir de un input (direccion parcial).
     * Ejemplo: GET v1/addresses/autocomplete?q=Calle%20de%20Al&lang=es-ES
     */
    @GetMapping("/autocomplete")
    public ResponseEntity<?> suggestions(
            @RequestParam("q") String q
    ) {
        if (q == null || q.trim().length() < 2) return ResponseEntity.ok(List.of());
        return ResponseEntity.ok(completeAddressUseCase.execute(q.trim()));
    }

    /**
     * POST /v1/addresses/addressConfirmation
     * normaliza y persiste UNA única dirección, por tratamiendo manual del cliente.
     *
     * Body: JSON con un AddressValidationRequestDTO.
     * Devuelve: 200 OK con ValidatedAddressResponseDTO.
     *
     */
    @PostMapping("/addressConfirmation")
    public ResponseEntity<?> confirmAddress(
            @RequestBody AddressValidationRequestDTO input,
            @PathVariable long id) {

        AddressValidationInput toValidate = addressRequestMapper.toModel(input);
        ValidatedAddress results = validateAddressUseCase.processAddress(toValidate);
        return ResponseEntity.ok(addressResponseMapper.toDTO(results));
    }

    /**
     * Geolocalización (Point) del recurso concreto.
     * Regla: PathParam para el recurso concreto + sub-recurso "geolocation".
     */
    @GetMapping("/{id}/geolocation/preview")
    public ResponseEntity<?> previewAddressByGeoLocation(
            @PathVariable long id,
            @RequestParam double lon,
            @RequestParam double lat) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(Map.of(
                        "status", "NOT_IMPLEMENTED",
                        "message", "Pending implementation: reverse geocoding preview for given coordinates",
                        "id", id
                ));
    }

    /**
     * Confirma el refinamiento manual por posición y persiste la geolocalización.
     * Usa lon/lat como fuente de verdad y addressFormatted para uso humano.
     *
     * Respuestas habituales: 200 (ok), 400 (parámetros inválidos),
     * 404 (id no encontrado), 409 (conflicto), 500 (error interno).
     *
     */
    @PatchMapping("/{id}/geolocation/confirmation")
    public ResponseEntity<?> confirmAddressByGeoLocation(
            @PathVariable long id,
            @RequestParam double lon,
            @RequestParam double lat,
            @RequestParam String addressFormatted) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(Map.of(
                        "status", "NOT_IMPLEMENTED",
                        "message", "Pending implementation: persist manual refinement using coords (source of truth) and the human-readable address",
                        "id", id,
                        "received", Map.of("lat", lat, "lon", lon, "addressFormatted", addressFormatted)
                ));
    }

    /**
     * Validación de direcciones desde fichero (CSV/XLSX/XLS).
     * Ejemplo: POST /v1/addresses/fileValidations (multipart)
     */
    @PostMapping(
            value = "/addressfileValidations",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> validateFromFile(
            @RequestPart("file") MultipartFile file,
            @RequestParam(name = "locale", required = false) String localeHint,
            @RequestParam(name = "async", defaultValue = "false") boolean async
    ) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(Map.of(
                        "status", "NOT_IMPLEMENTED",
                        "message", "Pending implementation: validation from uploaded file",
                        "fileName", file.getOriginalFilename(),
                        "locale", localeHint,
                        "async", async
                ));
    }

    /**
     * Recurso concreto por ID usando PathParam.
     * Ejemplo: GET /v1/addresses/123456
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> addressById(@PathVariable("id") Long idDireccion) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(Map.of(
                        "status", "NOT_IMPLEMENTED",
                        "message", "Pending implementation: fetch single address by ID",
                        "id", idDireccion
                ));
    }
}
