package com.taskmanager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for REST APIs (needed for Postman / frontend JS fetch calls)
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth
                // Allow public access to static files and user registration
                .requestMatchers("/", "/index.html", "/css/**", "/js/**").permitAll()
                .requestMatchers("/api/users/register").permitAll()
                // Everything else requires authentication
                .anyRequest().authenticated()
            )

            // Use HTTP Basic auth (simple username/password header)
            // In a real app you'd replace this with JWT tokens
            .httpBasic(basic -> {});

        return http.build();
    }

    // BCrypt password hasher — this bean is injected into UserService
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
