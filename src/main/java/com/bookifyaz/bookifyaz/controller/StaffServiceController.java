package com.bookifyaz.bookifyaz.controller;

import com.bookifyaz.bookifyaz.dto.response.ServiceResponse;
import com.bookifyaz.bookifyaz.dto.response.StaffResponse;
import com.bookifyaz.bookifyaz.service.StaffServicesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public")
@CrossOrigin
public class StaffServiceController {
    private final StaffServicesService servicesService;

    public StaffServiceController(StaffServicesService servicesService) {
        this.servicesService = servicesService;
    }

    @GetMapping("/services")
    public ResponseEntity<List<ServiceResponse>> getServices() {
        return ResponseEntity.ok(servicesService.findServicesByTenant());
    }

    @GetMapping("/staff-by-service/{serviceId}")
    public ResponseEntity<List<StaffResponse>> getStaffByService(@PathVariable int serviceId) {
        return ResponseEntity.ok(servicesService.findStaffByServiceId(serviceId));
    }
}
