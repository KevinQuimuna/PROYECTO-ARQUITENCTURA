package com.logiflow.ruteo.api;

import java.time.OffsetDateTime;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> status(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        return ResponseEntity.status(status)
                .body(Map.of(
                        "timestamp",
                        OffsetDateTime.now().toString(),
                        "error",
                        status.getReasonPhrase(),
                        "message",
                        ex.getReason() != null ? ex.getReason() : status.getReasonPhrase()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> general(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "timestamp",
                        OffsetDateTime.now().toString(),
                        "error",
                        "INTERNAL_SERVER_ERROR",
                        "message",
                        ex.getMessage() != null ? ex.getMessage() : "Error interno del servidor",
                        "type",
                        ex.getClass().getSimpleName()));
    }
}
