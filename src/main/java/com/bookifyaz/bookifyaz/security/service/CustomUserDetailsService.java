package com.bookifyaz.bookifyaz.security.service;

import com.bookifyaz.bookifyaz.entity.User;
import com.bookifyaz.bookifyaz.repository.UserRepository;
import com.bookifyaz.bookifyaz.security.model.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //todo custom exception elave edilecek
        User user = userRepository.findByEmail(username).orElseThrow(
                () -> new RuntimeException("Email not found: " + username)
        );

        return new CustomUserDetails(user);

    }
}
