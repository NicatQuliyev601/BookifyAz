package com.bookifyaz.bookifyaz.config;

import com.bookifyaz.bookifyaz.repository.TenantRepository;
import com.bookifyaz.bookifyaz.security.filter.AuthRequestFilter;
import com.bookifyaz.bookifyaz.security.service.TokenAuthService;
import com.bookifyaz.bookifyaz.tenant.TenantResolutionFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final TokenAuthService tokenAuthService;
    private final TenantRepository tenantRepository;

    public SecurityConfig(TokenAuthService tokenAuthService, TenantRepository tenantRepository) {
        this.tokenAuthService = tokenAuthService;
        this.tenantRepository = tenantRepository;
    }

    @Bean
    public TenantResolutionFilter tenantResolutionFilter() {
        return new TenantResolutionFilter(tenantRepository);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/public/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/ws/**").permitAll()
                .requestMatchers("/api/v1/**").authenticated()
                .anyRequest().authenticated()
            )
            .addFilterBefore(tenantResolutionFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new AuthRequestFilter(tokenAuthService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
