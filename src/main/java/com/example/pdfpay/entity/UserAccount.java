package com.example.pdfpay.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "user_accounts", indexes = {@Index(name="idx_user_email", columnList="email", unique=true)})
public class UserAccount {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, unique=true, length=190) private String email;
    @Column(nullable=false, length=80) private String displayName;
    @Column(length=255) private String passwordHash;
    @Column(nullable=false, length=30) private String provider = "LOCAL";
    @Column(nullable=false) private Instant createdAt;
    @Column(nullable=false) private boolean enabled = true;

    public Long getId(){return id;} public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public String getDisplayName(){return displayName;} public void setDisplayName(String v){displayName=v;}
    public String getPasswordHash(){return passwordHash;} public void setPasswordHash(String v){passwordHash=v;}
    public String getProvider(){return provider;} public void setProvider(String v){provider=v;}
    public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;}
    public boolean isEnabled(){return enabled;} public void setEnabled(boolean v){enabled=v;}
}
