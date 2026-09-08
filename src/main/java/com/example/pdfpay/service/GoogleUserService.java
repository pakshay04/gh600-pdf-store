package com.example.pdfpay.service;

import com.example.pdfpay.entity.UserAccount;
import com.example.pdfpay.repository.UserAccountRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Locale;

@Service
public class GoogleUserService extends DefaultOAuth2UserService {
    private final UserAccountRepository repo;
    public GoogleUserService(UserAccountRepository repo){this.repo=repo;}
    @Override public OAuth2User loadUser(OAuth2UserRequest request) {
        OAuth2User principal=super.loadUser(request);
        String email=principal.getAttribute("email");
        if(email==null || email.isBlank()) throw new OAuth2AuthenticationException(new OAuth2Error("google_email_missing"),"Google did not provide an email address");
        String name=principal.getAttribute("name");
        UserAccount u=repo.findByEmailIgnoreCase(email).orElseGet(() -> {
            UserAccount n=new UserAccount(); n.setEmail(email.toLowerCase(Locale.ROOT)); n.setDisplayName(name==null?email.split("@")[0]:name); n.setProvider("GOOGLE"); n.setCreatedAt(Instant.now()); n.setEnabled(true); return repo.save(n);
        });
        if(!"GOOGLE".equals(u.getProvider())) { u.setProvider("LOCAL+GOOGLE"); repo.save(u); }
        return principal;
    }
}
