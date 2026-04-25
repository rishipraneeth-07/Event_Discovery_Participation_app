package com.college.eventapp.config;

import com.college.eventapp.security.JsonAccessDeniedHandler;
import com.college.eventapp.security.JsonAuthenticationEntryPoint;
import com.college.eventapp.security.TokenAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final TokenAuthenticationFilter tokenAuthenticationFilter;
    private final JsonAuthenticationEntryPoint authenticationEntryPoint;
    private final JsonAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(TokenAuthenticationFilter tokenAuthenticationFilter,
                          JsonAuthenticationEntryPoint authenticationEntryPoint,
                          JsonAccessDeniedHandler accessDeniedHandler) {
        this.tokenAuthenticationFilter = tokenAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST,
                                "/api/auth/register",
                                "/api/auth/login",
                                "/api/auth/forgot-password",
                                "/api/auth/reset-password").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/events",
                                "/api/events/search",
                                "/api/events/*",
                                "/api/app-content/**",
                                "/api/app/content/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/events").hasAnyRole("ORGANIZER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/events/*").hasAnyRole("ORGANIZER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/events/*").hasAnyRole("ORGANIZER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/events/pending").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/events/*/approve").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/events/*/reject").hasRole("ADMIN")
                        .requestMatchers("/api/events/organizer/**").hasAnyRole("ORGANIZER", "ADMIN")
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().authenticated()
                );
        http.addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("http://10.0.2.2:*", "http://localhost:*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
