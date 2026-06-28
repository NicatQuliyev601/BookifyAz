package com.bookifyaz.bookifyaz.repository;

import com.bookifyaz.bookifyaz.entity.Staff;
import com.bookifyaz.bookifyaz.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, Integer> {
    List<Staff> findStaffByTenant(Tenant tenant);
}
