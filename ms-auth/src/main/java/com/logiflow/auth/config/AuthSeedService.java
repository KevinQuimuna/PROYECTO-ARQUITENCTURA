package com.logiflow.auth.config;

import com.logiflow.auth.domain.RoleEntity;
import com.logiflow.auth.domain.RoleRepository;
import com.logiflow.auth.domain.UserEntity;
import com.logiflow.auth.domain.UserRepository;
import java.util.HashSet;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthSeedService {

    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder encoder;

    public AuthSeedService(UserRepository users, RoleRepository roles, PasswordEncoder encoder) {
        this.users = users;
        this.roles = roles;
        this.encoder = encoder;
    }

    @Transactional
    public void seed() {
        RoleEntity userRole = roles.findByName("ROLE_USER")
                .orElseGet(() -> roles.save(newRole("ROLE_USER")));
        RoleEntity adminRole = roles.findByName("ROLE_ADMIN")
                .orElseGet(() -> roles.save(newRole("ROLE_ADMIN")));

        if (users.findByUsername("admin").isPresent()) {
            return;
        }

        UserEntity admin = new UserEntity();
        admin.setUsername("admin");
        admin.setEmail("admin@logiflow.test");
        admin.setPasswordHash(encoder.encode("admin123"));
        admin.setEnabled(true);
        var adminRoles = new HashSet<RoleEntity>();
        adminRoles.add(adminRole);
        adminRoles.add(userRole);
        admin.setRoles(adminRoles);
        users.save(admin);
    }

    private static RoleEntity newRole(String name) {
        RoleEntity r = new RoleEntity();
        r.setName(name);
        return r;
    }
}
