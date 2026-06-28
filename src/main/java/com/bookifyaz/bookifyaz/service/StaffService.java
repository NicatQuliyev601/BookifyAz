package com.bookifyaz.bookifyaz.service;

import com.bookifyaz.bookifyaz.dto.response.ServiceResponse;
import com.bookifyaz.bookifyaz.dto.response.StaffResponse;
import com.bookifyaz.bookifyaz.entity.Tenant;
import com.bookifyaz.bookifyaz.repository.ServiceRepository;
import com.bookifyaz.bookifyaz.repository.StaffRepository;
import com.bookifyaz.bookifyaz.repository.TenantRepository;
import com.bookifyaz.bookifyaz.tenant.TenantContext;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StaffService {

    private final TenantRepository tenantRepository;
    private final StaffRepository staffRepository;

    public StaffService(TenantRepository tenantRepository, StaffRepository staffRepository) {
        this.tenantRepository = tenantRepository;
        this.staffRepository = staffRepository;
    }

    public List<StaffResponse> findStaffByTenant() {
        Integer tenantId = TenantContext.getCurrentTenantId();
        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow(
                () -> new RuntimeException("Tenant not found")
        );


        return staffRepository.findStaffByTenant(tenant)
                .stream()
                .map(staff -> new StaffResponse(
                        staff.getId(),
                        staff.getBio(),
                        staff.getPhotoUrl(),
                        staff.isActive()
                )).collect(Collectors.toList());
    }
}
