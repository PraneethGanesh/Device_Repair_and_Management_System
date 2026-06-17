package com.example.user_service.config;

import com.example.user_service.entity.Role;
import com.example.user_service.entity.User;
import com.example.user_service.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

public class AdminDataLoader implements ApplicationRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminDataLoader(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {

        // only insert if admin does not already exist
        if (!userRepository.existsByEmail("admin@devicesupply.com")) {

            User admin = new User();
            admin.setName("admin");
            admin.setEmail("admin@devicesupply.com");
            admin.setPassword(passwordEncoder.encode("admin@123"));
            admin.setRole(Role.ADMIN);

            userRepository.save(admin);
            System.out.println("Admin user created successfully");
        }
    }
}
