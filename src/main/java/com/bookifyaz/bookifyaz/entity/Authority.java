package com.bookifyaz.bookifyaz.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "authority")
public class Authority implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Enumerated(EnumType.STRING)
    UserAuthority authority;

    public String getAuthority() {
        return authority.name();
    }

    public Authority() {
    }

    public Authority(Long id, UserAuthority authority) {
        this.id = id;
        this.authority = authority;
    }
}