package com.bookifyaz.bookifyaz.service;

import com.bookifyaz.bookifyaz.dto.request.StaffRequest;
import com.bookifyaz.bookifyaz.dto.response.ServiceResponse;
import com.bookifyaz.bookifyaz.dto.response.StaffResponse;
import com.bookifyaz.bookifyaz.entity.Staff;
import com.bookifyaz.bookifyaz.entity.Tenant;
import com.bookifyaz.bookifyaz.entity.User;
import com.bookifyaz.bookifyaz.repository.ServiceRepository;
import com.bookifyaz.bookifyaz.repository.StaffRepository;
import com.bookifyaz.bookifyaz.repository.TenantRepository;
import com.bookifyaz.bookifyaz.repository.UserRepository;
import com.bookifyaz.bookifyaz.tenant.TenantContext;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StaffService {

    private final TenantRepository tenantRepository;
    private final StaffRepository staffRepository;
    private final UserRepository userRepository;

    public StaffService(TenantRepository tenantRepository, StaffRepository staffRepository, UserRepository userRepository) {
        this.tenantRepository = tenantRepository;
        this.staffRepository = staffRepository;
        this.userRepository = userRepository;
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
                        staff.getUser().getFullName()
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
}
