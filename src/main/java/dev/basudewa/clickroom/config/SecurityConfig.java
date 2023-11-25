package dev.basudewa.clickroom.config;

import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(request -> request
//                        .requestMatchers("/room/admin/**").hasRole("ADMIN")
//                        .requestMatchers("/schedule/admin/**").hasRole("ADMIN")
//                        .requestMatchers("/facility/admin/**").hasRole("ADMIN")
//                        .requestMatchers("/request/admin/**").hasRole("ADMIN")
//                        .anyRequest().authenticated()
//                )
//                .csrf(csrf -> csrf.disable())
//                .httpBasic(Customizer.withDefaults());
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/api/v1/auth/login", "/api/v1/auth/admin/register")
                        .permitAll()
                        .requestMatchers(
                                "/api/v1/room/admin/**",
                                "/api/v1/schedule/admin/**",
                                "/api/v1/facility/admin/**",
                                "/api/v1/request/admin/**",
                                "/api/v1/demo/admin",
                                "/api/v1/auth/admin/**")
                        .hasAuthority("ADMIN")
                        .anyRequest()
                        .authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
