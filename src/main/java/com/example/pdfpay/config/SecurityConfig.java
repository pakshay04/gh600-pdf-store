package com.example.pdfpay.config;

import com.example.pdfpay.service.GoogleOidcUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {
    @Value("${ADMIN_USERNAME:admin}") private String adminUsername;
    @Value("${ADMIN_PASSWORD:ChangeMe123!}") private String adminPassword;
    @Value("${google.oauth2.enabled:false}") private boolean googleEnabled;

    @Bean PasswordEncoder passwordEncoder(){ return new BCryptPasswordEncoder(); }

    @Bean UserDetailsService userDetailsService(PasswordEncoder encoder, com.example.pdfpay.service.CommunityUserDetailsService communityUsers){
        UserDetails admin=User.builder().username(adminUsername).password(encoder.encode(adminPassword)).roles("ADMIN").build();
        InMemoryUserDetailsManager admins=new InMemoryUserDetailsManager(admin);
        return username -> {
            if(username.equalsIgnoreCase(adminUsername)) return admins.loadUserByUsername(adminUsername);
            return communityUsers.loadUserByUsername(username);
        };
    }

    @Bean AuthenticationSuccessHandler loginSuccessHandler(){
        return (request,response,authentication) -> {
            boolean admin=authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            response.sendRedirect(admin ? "/admin.html" : "/community.html");
        };
    }

    @Bean SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationSuccessHandler successHandler, GoogleOidcUserService googleOidcUserService) throws Exception {
        CookieCsrfTokenRepository csrf=CookieCsrfTokenRepository.withHttpOnlyFalse();
        http.csrf(c -> c.csrfTokenRepository(csrf).ignoringRequestMatchers("/api/auth/signup"))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/","/index.html","/css/**","/js/**","/privacy.html","/terms.html","/refund.html","/contact.html","/about.html","/disclaimer.html","/editorial-policy.html","/faq.html","/resources.html","/certifications.html","/learn.html","/learn-topic.html","/quizzes.html","/buy.html","/store.html","/exams/**","/login.html","/signup.html","/community.html","/api/auth/signup","/api/auth/me","/oauth2/**","/login/oauth2/**","/api/pdfs/**","/api/payments/**","/api/download/**").permitAll()
                .requestMatchers("/admin-login.html","/admin/csrf","/admin/login").permitAll()
                .requestMatchers("/admin.html","/api/pdfs/upload","/api/pdfs/admin/**","/api/community/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/community/**","/api/auth/logout").authenticated()
                .anyRequest().permitAll())
            .formLogin(form -> form.loginPage("/login.html").loginProcessingUrl("/login").successHandler(successHandler).failureUrl("/login.html?error=true").permitAll())
            .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/?logout=true").permitAll());
        if(googleEnabled) {
            http.oauth2Login(oauth -> oauth.loginPage("/login.html").userInfoEndpoint(u -> u.oidcUserService(googleOidcUserService)).successHandler(successHandler));
        }
        // Keep the legacy admin form endpoint working with the same authentication filter.
        return http.build();
    }
}
