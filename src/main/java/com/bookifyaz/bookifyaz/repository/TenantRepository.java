package com.bookifyaz.bookifyaz.repository;

import com.bookifyaz.bookifyaz.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface TenantRepository extends JpaRepository<Tenant,Integer> {
    boolean existsBySlug(String slug);
    Optional<Tenant> findBySlug(String slug);
}
