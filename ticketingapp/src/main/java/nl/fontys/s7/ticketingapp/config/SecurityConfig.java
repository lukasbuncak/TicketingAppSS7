package nl.fontys.s7.ticketingapp.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

import java.util.List;

@Configuration
public class SecurityConfig {

    private static final String[] SWAGGER = {
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/swagger-ui/**"
    };
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of(
                "http://localhost:5174",
                "http://localhost:5173"// TODO: add your real frontend(s)
                // "https://app.example.com"
        ));
        cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE"));
        cfg.setAllowedHeaders(List.of("Authorization","Content-Type"));
        cfg.setAllowCredentials(false);           // JWT in header, no cookies
        cfg.setMaxAge(600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    @Bean
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
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers("/admin/**").permitAll() //TODO: Temporary, remove later in production
                        // everything else requires a valid Bearer token
                        .anyRequest().authenticated()
                )

                .exceptionHandling(ex -> ex.authenticationEntryPoint(entryPoint))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}


