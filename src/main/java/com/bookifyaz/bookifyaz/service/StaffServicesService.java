package com.bookifyaz.bookifyaz.service;

import com.bookifyaz.bookifyaz.dto.request.ServiceRequest;
import com.bookifyaz.bookifyaz.dto.response.ServiceResponse;
import com.bookifyaz.bookifyaz.dto.response.StaffResponse;
import com.bookifyaz.bookifyaz.entity.StaffService;
import com.bookifyaz.bookifyaz.entity.Tenant;
import com.bookifyaz.bookifyaz.repository.ServiceRepository;
import com.bookifyaz.bookifyaz.repository.StaffRepository;
import com.bookifyaz.bookifyaz.repository.StaffServiceRepository;
import com.bookifyaz.bookifyaz.repository.TenantRepository;
import com.bookifyaz.bookifyaz.tenant.TenantContext;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StaffServicesService {

    private final TenantRepository tenantRepository;
    private final ServiceRepository serviceRepository;
    private final StaffServiceRepository staffServiceRepository;
    private final ModelMapper modelMapper;

    public StaffServicesService(TenantRepository tenantRepository, ServiceRepository serviceRepository, StaffServiceRepository staffServiceRepository, ModelMapper modelMapper) {
        this.tenantRepository = tenantRepository;
        this.serviceRepository = serviceRepository;
        this.staffServiceRepository = staffServiceRepository;
        this.modelMapper = modelMapper;
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
                .map(staff -> new StaffResponse(staff.getId(), staff.getBio(), staff.getPhotoUrl(), staff.isActive(), staff.getUser().getFullName()))
                .collect(Collectors.toList());
    }

    public String createService(ServiceRequest request) {
        Integer tenantId = TenantContext.getCurrentTenantId();
        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow(
                () -> new RuntimeException("Tenant not found")
        );
        if (serviceRepository.existsByName(request.name())) {
            //todo custom exception
            throw new RuntimeException("Service Already exist");
        }

        com.bookifyaz.bookifyaz.entity.Service service = new com.bookifyaz.bookifyaz.entity.Service();
        service.setName(request.name());
        service.setMinDuration(request.minDuration());
        service.setPrice(request.price());
        service.setColor(request.color());
        service.setActive(request.isActive());
        service.setTenant(tenant);
        serviceRepository.save(service);

        return "Service created";
    }

    public ServiceResponse editService(ServiceRequest request, int serviceId) {
        com.bookifyaz.bookifyaz.entity.Service service = serviceRepository.findById(serviceId).orElseThrow(
                () -> new RuntimeException("Service not found")
        );

        if (Objects.nonNull(request.name())
                && !"".equalsIgnoreCase(request.name())) {
            service.setName(request.name());
        }

        if (Objects.nonNull(request.price())
                && !"".equalsIgnoreCase(String.valueOf(request.price()))) {
            service.setPrice(request.price());
        }

        if (Objects.nonNull(request.minDuration())) {
            service.setMinDuration(request.minDuration());
        }

        if (Objects.nonNull(request.color())
                && !"".equalsIgnoreCase(request.color())) {
            service.setColor(request.color());
        }

        if (!"".equalsIgnoreCase(String.valueOf(request.isActive()))) {
            service.setActive(request.isActive());
        }

        com.bookifyaz.bookifyaz.entity.Service saveService = serviceRepository.save(service);


        return new ServiceResponse(
                saveService.getId(),
                saveService.getName(),
                saveService.getMinDuration(),
                saveService.getPrice(),
                saveService.getColor(),
                saveService.isActive()
        );
    }

    @Transactional
    //todo there is a problem here i will look. fk problem.
    public String deleteService(int serviceId) {
        com.bookifyaz.bookifyaz.entity.Service service = serviceRepository.findById(serviceId).orElseThrow(
                () -> new RuntimeException("Service not found")
        );
        staffServiceRepository.deleteAllByServiceId(serviceId);
        serviceRepository.delete(service);

        return "Service deleted successfully";
    }

}
