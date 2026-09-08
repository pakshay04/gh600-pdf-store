package com.example.pdfpay.service;

import com.example.pdfpay.entity.UserAccount;
import com.example.pdfpay.repository.UserAccountRepository;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Locale;

@Service
public class GoogleOidcUserService extends OidcUserService {
    private final UserAccountRepository repo;
    public GoogleOidcUserService(UserAccountRepository repo){this.repo=repo;}
    @Override public OidcUser loadUser(OidcUserRequest request) throws OAuth2AuthenticationException {
        OidcUser principal=super.loadUser(request);
        String email=principal.getEmail();
        if(email==null || email.isBlank()) throw new OAuth2AuthenticationException(new OAuth2Error("google_email_missing"),"Google did not provide an email address");
        UserAccount u=repo.findByEmailIgnoreCase(email).orElseGet(() -> {
            UserAccount n=new UserAccount(); n.setEmail(email.toLowerCase(Locale.ROOT)); n.setDisplayName(principal.getFullName()==null?email.substring(0,email.indexOf('@')):principal.getFullName()); n.setProvider("GOOGLE"); n.setCreatedAt(Instant.now()); n.setEnabled(true); return repo.save(n);
        });
        if(u.getProvider().equals("LOCAL")){u.setProvider("LOCAL+GOOGLE"); repo.save(u);}
        return principal;
    }
}
