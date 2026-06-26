package com.bookifyaz.bookifyaz.security.filter;

import com.bookifyaz.bookifyaz.security.service.TokenAuthService;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//@Slf4j
public class AuthFilterConfigurerAdapter extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final TokenAuthService tokenAuthService;

    public AuthFilterConfigurerAdapter(TokenAuthService tokenAuthService) {
        this.tokenAuthService = tokenAuthService;
    }

    @Override
    public void configure(HttpSecurity http) {
//        log.trace("Added auth request filter");
        http.addFilterBefore(new AuthRequestFilter(tokenAuthService), UsernamePasswordAuthenticationFilter.class);
    }
}