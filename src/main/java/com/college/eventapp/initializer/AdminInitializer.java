package com.college.eventapp.initializer;

import com.college.eventapp.model.Role;
import com.college.eventapp.model.User;
import com.college.eventapp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AdminInitializer {

    @Bean
    CommandLineRunner createAdmin(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByEmail("admin@event.com").isEmpty()) {

                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

                User admin = new User();
                admin.setName("Admin");
                admin.setEmail("admin@event.com");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRole(Role.valueOf("ADMIN"));
                userRepository.save(admin);

                System.out.println("Default admin created");
            }
        };
    }
}