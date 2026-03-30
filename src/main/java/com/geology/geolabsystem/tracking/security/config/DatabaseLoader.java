package com.geology.geolabsystem.tracking.security.config;

import com.geology.geolabsystem.tracking.security.entity.UserEntity;
import com.geology.geolabsystem.tracking.security.entity.enums.Role;
import com.geology.geolabsystem.tracking.security.repository.UserRepository;
import jakarta.persistence.EntityListeners;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
@EntityListeners(AuditingEntityListener.class)
public class DatabaseLoader {
    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                UserEntity admin = new UserEntity();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("password"));

                admin.setFullName("Admin Adminovich");
                admin.setRoles(Set.of(Role.TEAM_LEAD));

                userRepository.save(admin);

                System.out.println("#################################################");
                System.out.println(">>> ТЕСТОВЫЙ ПОЛЬЗОВАТЕЛЬ СОЗДАН:");
                System.out.println(">>> Login: admin");
                System.out.println(">>> Password: password");
                System.out.println("#################################################");
            }
        };
    }
}
