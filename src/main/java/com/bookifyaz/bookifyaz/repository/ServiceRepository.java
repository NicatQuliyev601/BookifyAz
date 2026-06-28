package com.bookifyaz.bookifyaz.repository;

import com.bookifyaz.bookifyaz.entity.Service;
import com.bookifyaz.bookifyaz.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ServiceRepository extends JpaRepository<Service, Integer> {
    List<Service> getServicesByTenant(Tenant tenant);
}
