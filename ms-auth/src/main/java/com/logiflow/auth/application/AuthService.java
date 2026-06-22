package com.logiflow.auth.application;

import com.logiflow.auth.api.dto.LoginRequest;
import com.logiflow.auth.api.dto.LoginResponse;
import com.logiflow.auth.api.dto.RegisterRequest;
import com.logiflow.auth.api.dto.VerifyRequest;
import com.logiflow.auth.api.dto.VerifyResponse;
import com.logiflow.auth.domain.RoleEntity;
import com.logiflow.auth.domain.RoleRepository;
import com.logiflow.auth.domain.UserEntity;
import com.logiflow.auth.domain.UserRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(
            UserRepository users, RoleRepository roles, PasswordEncoder encoder, JwtService jwt) {
        this.users = users;
        this.roles = roles;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    @Transactional
    public void register(RegisterRequest req) {
        if (users.existsByUsernameOrEmail(req.username(), req.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Usuario o email ya existe");
        }
        String roleName = normalizarRol(req.role());
        RoleEntity role = roles
                .findByName(roleName)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Rol inválido. Use ROLE_USER o ROLE_ADMIN"));
        UserEntity u = new UserEntity();
        u.setUsername(req.username());
        u.setEmail(req.email());
        u.setPasswordHash(encoder.encode(req.password()));
        u.getRoles().add(role);
        users.save(u);
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest req) {
        UserEntity u = users
                .findByUsername(req.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));
        if (!encoder.matches(req.password(), u.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }
        List<String> roleNames = u.getRoles().stream().map(RoleEntity::getName).toList();
        String token = jwt.generateToken(u.getUsername(), roleNames);
        return new LoginResponse(token, u.getUsername(), roleNames);
    }

    public VerifyResponse verify(VerifyRequest req) {
        if (!jwt.isValid(req.token())) {
            return new VerifyResponse(false, null, List.of());
        }
        var claims = jwt.parse(req.token());
        return new VerifyResponse(true, claims.getSubject(), jwt.rolesFromClaims(claims));
    }

    private static String normalizarRol(String role) {
        if (role == null || role.isBlank()) {
            return "ROLE_USER";
        }
        return role.startsWith("ROLE_") ? role : "ROLE_" + role;
    }
}
