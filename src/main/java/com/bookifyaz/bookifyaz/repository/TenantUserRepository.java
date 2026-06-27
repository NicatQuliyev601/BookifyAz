package com.bookifyaz.bookifyaz.repository;

import com.bookifyaz.bookifyaz.entity.Tenant;
import com.bookifyaz.bookifyaz.entity.TenantUser;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TenantUserRepository extends JpaRepository<TenantUser,Integer> {
}
