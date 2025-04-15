package org.example.diplom.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationProvider;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationProvider alwaysAllowAuthenticationProvider;

    public SecurityConfig(AuthenticationProvider alwaysAllowAuthenticationProvider) {
        this.alwaysAllowAuthenticationProvider = alwaysAllowAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(alwaysAllowAuthenticationProvider)
                .authorizeRequests(authorize -> authorize
                        .requestMatchers("/", "/login", "/register", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }
}