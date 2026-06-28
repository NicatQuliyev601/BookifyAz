package com.bookifyaz.bookifyaz.service;

import com.bookifyaz.bookifyaz.dto.response.ServiceResponse;
import com.bookifyaz.bookifyaz.dto.response.StaffResponse;
import com.bookifyaz.bookifyaz.entity.Tenant;
import com.bookifyaz.bookifyaz.repository.ServiceRepository;
import com.bookifyaz.bookifyaz.repository.StaffRepository;
import com.bookifyaz.bookifyaz.repository.StaffServiceRepository;
import com.bookifyaz.bookifyaz.repository.TenantRepository;
import com.bookifyaz.bookifyaz.tenant.TenantContext;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StaffServicesService {

    private final TenantRepository tenantRepository;
    private final ServiceRepository serviceRepository;
    private final StaffServiceRepository staffServiceRepository;

    public StaffServicesService(TenantRepository tenantRepository, ServiceRepository serviceRepository, StaffServiceRepository staffServiceRepository) {
        this.tenantRepository = tenantRepository;
        this.serviceRepository = serviceRepository;
        this.staffServiceRepository = staffServiceRepository;
    }

    public List<ServiceResponse> findServicesByTenant() {
        Integer tenantId = TenantContext.getCurrentTenantId();
        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow(
                () -> new RuntimeException("Tenant not found")
        );


        return serviceRepository.getServicesByTenant(tenant)
                .stream()
                .map(service -> new ServiceResponse(
                        service.getId(),
                        service.getName(),
                        service.getMinDuration(),
                        service.getPrice(),
                        service.getColor(),
                        service.isActive()
                )).collect(Collectors.toList());
    }

    public List<StaffResponse> findStaffByServiceId(int serviceId) {
        Integer tenantId = TenantContext.getCurrentTenantId();
        return staffServiceRepository.findStaffByServiceIdAndTenantId(serviceId, tenantId)
                .stream()
                .map(staff -> new StaffResponse(staff.getId(),staff.getBio(), staff.getPhotoUrl(), staff.isActive()))
                .collect(Collectors.toList());
    }

}
