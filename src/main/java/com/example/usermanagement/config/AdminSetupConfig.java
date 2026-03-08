package com.example.usermanagement.config;

import com.example.usermanagement.user.User;
import com.example.usermanagement.user.UserRepository;
import com.example.usermanagement.user.UserRole;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminSetupConfig {

    @Bean
    CommandLineRunner adminSeed(UserRepository userRepository) {
        return args -> {
            String adminEmail = "admin@example.com";
            if (!userRepository.existsByEmail(adminEmail)) {
                User admin = new User("System", "Admin", adminEmail);
                admin.setRole(UserRole.ADMIN);
                userRepository.save(admin);
            }
        };
    }
}
