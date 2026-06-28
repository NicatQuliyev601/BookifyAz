package com.bookifyaz.bookifyaz.repository;

import com.bookifyaz.bookifyaz.entity.Tenant;
import com.bookifyaz.bookifyaz.entity.TenantSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface TenantSubscriptionRepository extends JpaRepository<TenantSubscription,Integer> {
    Optional<TenantSubscription> findActiveStatusById(int tenantId);
}
