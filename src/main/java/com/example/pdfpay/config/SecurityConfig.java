package com.example.pdfpay.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Value("${ADMIN_USERNAME}")
    private String adminUsername;

    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(
            PasswordEncoder passwordEncoder) {

        UserDetails admin = User.builder()
                .username(adminUsername)
                .password(passwordEncoder.encode(adminPassword))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(admin);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth

                        // Customer website
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/css/**",
                                "/js/app.js",
                                "/privacy.html",
                                "/terms.html",
                                "/refund.html",
                                "/contact.html",
                                "/api/pdfs/**",
                                "/api/payments/**",
                                "/api/download/**"
                        ).permitAll()

                        // Admin
                        .requestMatchers(
                                "/admin.html",
                                "/js/admin.js",
                                "/api/pdfs/upload"
                        ).hasRole("ADMIN")

                        .anyRequest().permitAll()
                )

                .formLogin(form -> form
                        .loginPage("/admin-login.html")
                        .loginProcessingUrl("/admin/login")
                        .defaultSuccessUrl("/admin.html", true)
                        .failureUrl("/admin-login.html?error=true")
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/admin/logout")
                        .logoutSuccessUrl("/admin-login.html")
                        .permitAll()
                )

                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(
                                "/api/payments/**",
                                "/api/download/**"
                        )
                );

        return http.build();
    }
}