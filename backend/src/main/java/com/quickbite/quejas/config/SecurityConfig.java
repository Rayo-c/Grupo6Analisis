package com.quickbite.quejas.config;

import com.quickbite.quejas.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuracion de seguridad. Autorizacion por rol segun RN01:
 * ROLE_CLIENTE, ROLE_AGENTE, ROLE_SUPERVISOR, ROLE_ADMIN.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // CU00 Portal, CU01 Login, CU02 Registro, CU03 Recuperar Password: publicos
                        .requestMatchers("/api/auth/**", "/api/portal/**").permitAll()
                        // CU07 Registrar Queja - invitado permitido (FA01)
                        .requestMatchers("/api/quejas/invitado/**", "/api/quejas/seguimiento/**").permitAll()

                        // CU05 / CU06 - catalogos: consulta publica, escritura solo ADMIN
                        .requestMatchers("GET", "/api/sucursales/**", "/api/categorias/**").permitAll()
                        .requestMatchers("/api/sucursales/**", "/api/categorias/**").hasAuthority("ROLE_ADMIN")

                        // CU04 - Administrar Cuentas: solo ADMIN
                        .requestMatchers("/api/usuarios/**").hasAuthority("ROLE_ADMIN")

                        // CU15 - Reportes: SUPERVISOR y ADMIN
                        .requestMatchers("/api/reportes/**").hasAnyAuthority("ROLE_SUPERVISOR", "ROLE_ADMIN")

                        // CU09 (reasignacion manual): SUPERVISOR
                        .requestMatchers("/api/quejas/*/reasignar").hasAuthority("ROLE_SUPERVISOR")

                        // CU11 Resolver, CU10 Seguimiento, CU12 Escalar: AGENTE y SUPERVISOR
                        .requestMatchers("/api/quejas/*/seguimiento", "/api/quejas/*/resolver", "/api/quejas/*/escalar")
                            .hasAnyAuthority("ROLE_AGENTE", "ROLE_SUPERVISOR")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(allowedOrigins.split(",")));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
