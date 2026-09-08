package com.example.pdfpay.controller;

import com.example.pdfpay.entity.UserAccount;
import com.example.pdfpay.repository.UserAccountRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserAccountRepository repo; private final PasswordEncoder encoder;
    @Value("${google.oauth2.enabled:false}") private boolean googleEnabled;
    public AuthController(UserAccountRepository repo, PasswordEncoder encoder){this.repo=repo;this.encoder=encoder;}
    public record SignupRequest(@NotBlank @Size(min=2,max=80) String name,@NotBlank @Email String email,@NotBlank @Size(min=8,max=100) String password){}
    @PostMapping("/signup") public ResponseEntity<?> signup(@RequestBody SignupRequest r){
        String email=r.email().trim().toLowerCase();
        if(repo.existsByEmailIgnoreCase(email)) return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error","An account already exists for this email."));
        UserAccount u=new UserAccount(); u.setDisplayName(r.name().trim());u.setEmail(email);u.setPasswordHash(encoder.encode(r.password()));u.setProvider("LOCAL");u.setCreatedAt(Instant.now());u.setEnabled(true);repo.save(u);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message","Account created. You can now sign in."));
    }
    @GetMapping("/config") public Map<String,Object> config(){ return Map.of("googleEnabled",googleEnabled); }

    @GetMapping("/me") public Map<String,Object> me(HttpServletRequest req){
        var a=req.getUserPrincipal(); if(a==null) return Map.of("authenticated",false);
        String email=a.getName();
        if(a instanceof org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken) {
            Object principal=((org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken)a).getPrincipal();
            if(principal instanceof org.springframework.security.oauth2.core.oidc.user.OidcUser oidc && oidc.getEmail()!=null) email=oidc.getEmail();
            else if(principal instanceof org.springframework.security.oauth2.core.user.OAuth2User oauth && oauth.getAttribute("email")!=null) email=oauth.getAttribute("email");
        }
        var u=repo.findByEmailIgnoreCase(email);
        if(u.isEmpty()) return Map.of("authenticated",true,"name",email);
        return Map.of("authenticated",true,"name",u.get().getDisplayName(),"email",u.get().getEmail(),"provider",u.get().getProvider());
    }
}
