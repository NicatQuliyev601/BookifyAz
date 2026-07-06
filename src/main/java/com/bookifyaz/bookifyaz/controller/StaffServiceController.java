package com.bookifyaz.bookifyaz.controller;

import com.bookifyaz.bookifyaz.dto.request.ServiceRequest;
import com.bookifyaz.bookifyaz.dto.response.ServiceResponse;
import com.bookifyaz.bookifyaz.dto.response.StaffResponse;
import com.bookifyaz.bookifyaz.service.StaffServicesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/services")
@CrossOrigin
public class StaffServiceController {

    private final StaffServicesService servicesService;

    public StaffServiceController(StaffServicesService servicesService) {
        this.servicesService = servicesService;
    }

    @PostMapping
    public ResponseEntity<String> createServices(@RequestBody ServiceRequest request) {
        return new ResponseEntity<>(servicesService.createService(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse> updateServices(@RequestBody ServiceRequest request,
                                                          @PathVariable("id") int serviceId) {
        return new ResponseEntity<>(servicesService.editService(request, serviceId), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") int serviceId) {
        return new ResponseEntity<>(servicesService.deleteService(serviceId), HttpStatus.OK);
    }
}
