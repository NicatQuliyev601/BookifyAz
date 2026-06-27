package com.bookifyaz.bookifyaz.service;

import com.bookifyaz.bookifyaz.dto.request.LoginRequest;
import com.bookifyaz.bookifyaz.dto.request.RegisterRequest;
import com.bookifyaz.bookifyaz.dto.response.LoginResponse;
import com.bookifyaz.bookifyaz.dto.response.RegisterResponse;
import com.bookifyaz.bookifyaz.entity.*;
import com.bookifyaz.bookifyaz.repository.AuthorityRepository;
import com.bookifyaz.bookifyaz.repository.TenantRepository;
import com.bookifyaz.bookifyaz.repository.TenantUserRepository;
import com.bookifyaz.bookifyaz.repository.UserRepository;
import com.bookifyaz.bookifyaz.security.model.CustomUserDetails;
import com.bookifyaz.bookifyaz.security.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TenantRepository tenantRepository;
    private final TenantUserRepository tenantUserRepository;


    public AuthService(UserRepository userRepository, AuthorityRepository authorityRepository,
                       BCryptPasswordEncoder passwordEncoder, @Lazy AuthenticationManager authenticationManager,
                       JwtService jwtService, TenantRepository tenantRepository,
                       TenantUserRepository tenantUserRepository) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.tenantRepository = tenantRepository;
        this.tenantUserRepository = tenantUserRepository;
    }

    public LoginResponse login(LoginRequest request) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.email(),
                request.password()
        ));

        CustomUserDetails userDetails = (CustomUserDetails) authenticate.getPrincipal();

        assert userDetails != null;
        String token = jwtService.issueToken(userDetails);

        return new LoginResponse(token, "Bearer");
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already exists");
        }
        if (tenantRepository.existsBySlug(request.subDomain())) {
            throw new RuntimeException("Subdomain already taken");
        }

        Authority ownerAuthority = authorityRepository.findByAuthority(UserAuthority.OWNER)
                .orElseGet(() -> {
                    Authority authority = new Authority();
                    authority.setAuthority(UserAuthority.OWNER);
                    return authorityRepository.save(authority);
                });

        User user = new User(
                request.fullName(),
                request.email(),
                request.phone(),
                passwordEncoder.encode(request.password()),
                List.of(ownerAuthority)
        );
        userRepository.save(user);

        Tenant tenant = new Tenant();
        tenant.setName(request.businessName());
        tenant.setSlug(request.subDomain());
        tenant.setTimezone(request.timezone());
        tenant.setActive(true);
        tenantRepository.save(tenant);

        TenantUser tenantUser = new TenantUser();
        tenantUser.setUser(user);
        tenantUser.setTenant(tenant);
        tenantUserRepository.save(tenantUser);

        return new RegisterResponse("Registration successful");
    }
}
