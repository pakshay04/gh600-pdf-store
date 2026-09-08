package com.example.pdfpay.service;

import com.example.pdfpay.entity.UserAccount;
import com.example.pdfpay.repository.UserAccountRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CommunityUserDetailsService {
    private final UserAccountRepository repo;
    public CommunityUserDetailsService(UserAccountRepository repo){this.repo=repo;}
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null) throw new UsernameNotFoundException("User not found");
        String key=username.trim();
        if (key.equalsIgnoreCase("admin")) throw new UsernameNotFoundException("Use admin console login");
        UserAccount u=repo.findByEmailIgnoreCase(key).orElseThrow(() -> new UsernameNotFoundException("Invalid email or password"));
        return User.withUsername(u.getEmail()).password(u.getPasswordHash()==null?"{noop}GOOGLE_ONLY":u.getPasswordHash()).roles("USER").disabled(!u.isEnabled()).build();
    }
}
