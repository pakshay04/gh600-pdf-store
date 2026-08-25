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
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

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

        CookieCsrfTokenRepository csrfRepository =
                CookieCsrfTokenRepository.withHttpOnlyFalse();

        http

                // ==========================================
                // CSRF
                // ==========================================

                .csrf(csrf -> csrf
                        .csrfTokenRepository(csrfRepository)
                )


                // ==========================================
                // AUTHORIZATION
                // ==========================================

                .authorizeHttpRequests(auth -> auth

                        // ------------------------------
                        // Public website
                        // ------------------------------

                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/css/**",
                                "/js/app.js",

                                "/privacy.html",
                                "/terms.html",
                                "/refund.html",
                                "/contact.html",

                                "/admin-login.html",
                                "/admin/csrf"
                        ).permitAll()


                        // ------------------------------
                        // Admin
                        // ------------------------------

                        .requestMatchers(
                                "/admin.html",
                                "/js/admin.js",
                                "/api/pdfs/upload"
                        ).hasRole("ADMIN")


                        // ------------------------------
                        // Customer
                        // ------------------------------

                        .requestMatchers(
                                "/api/pdfs/**",
                                "/api/payments/**",
                                "/api/download/**"
                        ).permitAll()


                        // Everything else
                        .anyRequest().permitAll()
                )


                // ==========================================
                // LOGIN
                // ==========================================

                .formLogin(form -> form

                        .loginPage("/admin-login.html")

                        .loginProcessingUrl("/admin/login")

                        .defaultSuccessUrl(
                                "/admin.html",
                                true
                        )

                        .failureUrl(
                                "/admin-login.html?error=true"
                        )

                        .permitAll()
                )


                // ==========================================
                // LOGOUT
                // ==========================================

                .logout(logout -> logout

                        .logoutUrl("/admin/logout")

                        .logoutSuccessUrl(
                                "/admin-login.html?logout=true"
                        )

                        .permitAll()
                );


        return http.build();
    }
}