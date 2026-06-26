package com.bookifyaz.bookifyaz.repository;

import com.bookifyaz.bookifyaz.entity.Authority;
import com.bookifyaz.bookifyaz.entity.UserAuthority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorityRepository extends JpaRepository<Authority, Integer> {
    Optional<Authority> findByAuthority(UserAuthority userAuthority);
}
