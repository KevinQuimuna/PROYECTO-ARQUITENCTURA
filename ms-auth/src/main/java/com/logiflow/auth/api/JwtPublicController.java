package com.logiflow.auth.api;

import com.logiflow.auth.api.dto.LoginRequest;
import com.logiflow.auth.api.dto.LoginResponse;
import com.logiflow.auth.api.dto.VerifyRequest;
import com.logiflow.auth.api.dto.VerifyResponse;
import com.logiflow.auth.application.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Auth (raíz)", description = "Alias de /api/auth/login y /api/auth/verify sin prefijo.")
public class JwtPublicController {

    private final AuthService authService;

    public JwtPublicController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Login (alias)", description = "Equivalente a POST /api/auth/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest body) {
        return authService.login(body);
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify (alias)", description = "Equivalente a POST /api/auth/verify")
    public VerifyResponse verify(@Valid @RequestBody VerifyRequest body) {
        return authService.verify(body);
    }
}
