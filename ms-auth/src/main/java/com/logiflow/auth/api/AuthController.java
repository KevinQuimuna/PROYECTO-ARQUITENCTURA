package com.logiflow.auth.api;

import com.logiflow.auth.api.dto.LoginRequest;
import com.logiflow.auth.api.dto.LoginResponse;
import com.logiflow.auth.api.dto.RegisterRequest;
import com.logiflow.auth.api.dto.VerifyRequest;
import com.logiflow.auth.api.dto.VerifyResponse;
import com.logiflow.auth.application.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth (prefijo /api/auth)", description = "Registro, login y verificación JWT.")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/health")
    @Operation(summary = "Estado del servicio")
    public Map<String, String> health() {
        return Map.of("status", "UP", "service", "ms-auth");
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar usuario", description = "Rol opcional: ROLE_USER (default) o ROLE_ADMIN.")
    public void register(@Valid @RequestBody RegisterRequest body) {
        authService.register(body);
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Devuelve token JWT. Demo: admin / admin123")
    public LoginResponse login(@Valid @RequestBody LoginRequest body) {
        return authService.login(body);
    }

    @PostMapping("/verify")
    @Operation(summary = "Verificar token JWT")
    public VerifyResponse verify(@Valid @RequestBody VerifyRequest body) {
        return authService.verify(body);
    }
}
