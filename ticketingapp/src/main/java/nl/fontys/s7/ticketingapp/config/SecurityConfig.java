package nl.fontys.s7.ticketingapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import java.util.List;

@Configuration
public class SecurityConfig {

    @Value("${app.cors.allowed-origins:*}")
    private List<String> allowedOrigins;
    private static final String[] SWAGGER = {
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/swagger-ui/**"
    };

    private final AuthenticationEntryPoint entryPoint;
    private final AuthenticationRequestFilter jwtFilter;

    public SecurityConfig(AuthenticationEntryPoint entryPoint,
                          AuthenticationRequestFilter jwtFilter) {
        this.entryPoint = entryPoint;
        this.jwtFilter = jwtFilter;
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOriginPatterns(allowedOrigins);
        cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE"));
        cfg.setAllowedHeaders(List.of("Authorization","Content-Type"));
        cfg.setAllowCredentials(false);           // JWT in header, no cookies
        cfg.setMaxAge(600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain adminChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/admin/**")
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(reg -> reg
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().hasRole("ADMIN")  // ROLE_ADMIN from Entra app role
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint(entryPoint))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(adminJwtAuthConverter()))
                );

        // NOTE: we DO NOT add AuthenticationRequestFilter here
        return http.build();
    }

    private JwtAuthenticationConverter adminJwtAuthConverter() {
        JwtGrantedAuthoritiesConverter rolesConverter = new JwtGrantedAuthoritiesConverter();
        rolesConverter.setAuthorityPrefix("ROLE_");
        rolesConverter.setAuthoritiesClaimName("roles"); // Entra app roles

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(rolesConverter);
        return converter;
    }

    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            AuthenticationEntryPoint entryPoint,
            AuthenticationRequestFilter jwtFilter) throws Exception {

        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy( SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(reg -> reg
                        // allow only CORS preflights (OPTIONS) for any path
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Static files (css/js) needed by Swagger UI
                        .requestMatchers( PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers(SWAGGER).permitAll()
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        // ONLY login is public
                        .requestMatchers(HttpMethod.POST, "/auth/login", "auth/mfa/verify").permitAll()
                        // everything else requires a valid Bearer token
                        .anyRequest().authenticated()
                )

                .exceptionHandling(ex -> ex.authenticationEntryPoint(entryPoint))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}


