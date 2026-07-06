package com.bookifyaz.bookifyaz.repository;

import com.bookifyaz.bookifyaz.entity.Staff;
import com.bookifyaz.bookifyaz.entity.StaffService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StaffServiceRepository extends JpaRepository<StaffService, Integer> {
    @Query("SELECT ss.staff FROM StaffService ss WHERE ss.service.id = :serviceId AND ss.staff.tenant.id = :tenantId")
    List<Staff> findStaffByServiceIdAndTenantId(@Param("serviceId") int serviceId, @Param("tenantId") int tenantId);

    void deleteAllByServiceId(int serviceId);
}
