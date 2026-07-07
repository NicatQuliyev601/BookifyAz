package com.bookifyaz.bookifyaz.service;

import com.bookifyaz.bookifyaz.dto.request.ServiceRequest;
import com.bookifyaz.bookifyaz.dto.request.StaffRequest;
import com.bookifyaz.bookifyaz.dto.response.ServiceResponse;
import com.bookifyaz.bookifyaz.dto.response.StaffResponse;
import com.bookifyaz.bookifyaz.dto.response.StaffServiceResponse;
import com.bookifyaz.bookifyaz.entity.Staff;
import com.bookifyaz.bookifyaz.entity.Tenant;
import com.bookifyaz.bookifyaz.entity.User;
import com.bookifyaz.bookifyaz.repository.*;
import com.bookifyaz.bookifyaz.tenant.TenantContext;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class StaffService {

    private final TenantRepository tenantRepository;
    private final StaffRepository staffRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final StaffServiceRepository staffServiceRepository;

    public StaffService(TenantRepository tenantRepository, StaffRepository staffRepository, UserRepository userRepository, ServiceRepository serviceRepository, StaffServiceRepository staffServiceRepository) {
        this.tenantRepository = tenantRepository;
        this.staffRepository = staffRepository;
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
        this.staffServiceRepository = staffServiceRepository;
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
                        staff.isActive(),
                        staff.getUser().getFullName(),
                        staff.getUser().getEmail(),
                        staff.getUser().getPhone()
                )).collect(Collectors.toList());
    }

    @Transactional
    public String createStaff(StaffRequest request) {
        Integer tenantId = TenantContext.getCurrentTenantId();
        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow(
                () -> new RuntimeException("Tenant not found")
        );

        if (userRepository.existsByEmail(request.email())) {
            //todo custom exception
            throw new RuntimeException("User already exist");
        }


        User user = new User();
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPhone(request.phoneNumber());
        userRepository.save(user);

        Staff staff = new Staff();
        staff.setBio(request.bio());
        staff.setPhotoUrl(request.photoUrl());
        staff.setActive(request.isActive());
        staff.setTenant(tenant);
        staff.setUser(user);
        staffRepository.save(staff);

        return "User created Successfully: " + staff;
    }

    public String assignServiceToStaff(int serviceId, int staffId) {
        Staff staff = staffRepository.findById(staffId).orElseThrow(
                //todo custom exception
                () -> new RuntimeException("Staff not found"));

        com.bookifyaz.bookifyaz.entity.Service service = serviceRepository.findById(serviceId).orElseThrow(
                //todo custom exception
                () -> new RuntimeException("Service not found"));

        com.bookifyaz.bookifyaz.entity.StaffService staffService = new com.bookifyaz.bookifyaz.entity.StaffService();
        staffService.setService(service);
        staffService.setStaff(staff);
        staffServiceRepository.save(staffService);

        return "service added to staff successfully";
    }

    public StaffResponse editStaff(StaffRequest request, int staffId) {
        Staff staff = staffRepository.findById(staffId).orElseThrow(
                //todo custom exception
                () -> new RuntimeException("Staff not found")
        );

        if (Objects.nonNull(request.fullName())
                && !"".equalsIgnoreCase(request.fullName())) {
            staff.getUser().setFullName(request.fullName());
        }

        if (Objects.nonNull(request.phoneNumber())
                && !"".equalsIgnoreCase(request.phoneNumber())) {
            staff.getUser().setPhone(request.phoneNumber());
        }

        if (Objects.nonNull(request.email())
                && !"".equalsIgnoreCase(request.email())) {
            staff.getUser().setEmail(request.email());
        }

        if (Objects.nonNull(request.bio())
                && !"".equalsIgnoreCase(request.bio())) {
            staff.setBio(request.bio());
        }

        if (Objects.nonNull(request.photoUrl())
                && !"".equalsIgnoreCase(request.photoUrl())) {
            staff.setPhotoUrl(request.photoUrl());
        }

        if (!"".equalsIgnoreCase(String.valueOf(request.isActive()))) {
            staff.setActive(request.isActive());
        }

        Staff saveStaff = staffRepository.save(staff);

        return new StaffResponse(
                saveStaff.getId(),
                saveStaff.getBio(),
                saveStaff.getPhotoUrl(),
                saveStaff.isActive(),
                saveStaff.getUser().getFullName(),
                saveStaff.getUser().getPhone(),
                saveStaff.getUser().getEmail()
        );
    }
}
