package com.logiflow.taller.api;

import java.time.OffsetDateTime;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;

@RestControllerAdvice
public class TallerExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validacion(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Validación");
        return ResponseEntity.badRequest()
                .body(Map.of("timestamp", OffsetDateTime.now().toString(), "error", "VALIDATION", "message", msg));
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<Map<String, Object>> flotaNoDisponible(ResourceAccessException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of(
                        "timestamp",
                        OffsetDateTime.now().toString(),
                        "error",
                        "FLOTA_NO_ALCANZABLE",
                        "message",
                        "No se pudo contactar ms-flota-rest. Verifique logiflow.flota.base-url y que el servicio esté en ejecución."));
    }
}
