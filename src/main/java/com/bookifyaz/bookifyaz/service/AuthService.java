package com.bookifyaz.bookifyaz.service;

import com.bookifyaz.bookifyaz.dto.request.LoginRequest;
import com.bookifyaz.bookifyaz.dto.request.RegisterRequest;
import com.bookifyaz.bookifyaz.dto.response.LoginResponse;
import com.bookifyaz.bookifyaz.dto.response.RegisterResponse;
import com.bookifyaz.bookifyaz.entity.Authority;
import com.bookifyaz.bookifyaz.entity.User;
import com.bookifyaz.bookifyaz.entity.UserAuthority;
import com.bookifyaz.bookifyaz.repository.AuthorityRepository;
import com.bookifyaz.bookifyaz.repository.UserRepository;
import com.bookifyaz.bookifyaz.security.model.CustomUserDetails;
import com.bookifyaz.bookifyaz.security.service.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    public AuthService(UserRepository userRepository, AuthorityRepository authorityRepository, BCryptPasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
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

    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            //todo custom exception will be written
            throw new RuntimeException("User already exist");
        }

        Authority byAuthority = authorityRepository.findByAuthority(UserAuthority.OWNER)
                .orElseGet(
                        () -> {
                            Authority authority = new Authority();
                            authority.setAuthority(UserAuthority.OWNER);
                            return authorityRepository.save(authority);
                        }
                );

        User user = new User(
                request.fullName(),
                request.email(),
                request.phone(),
                passwordEncoder.encode(request.password()),
                List.of(byAuthority)
        );

        userRepository.save(user);
        return new RegisterResponse("User registered successfully");

    }
}
