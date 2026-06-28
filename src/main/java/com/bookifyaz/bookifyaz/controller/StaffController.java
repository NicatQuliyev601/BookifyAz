package com.bookifyaz.bookifyaz.controller;

import com.bookifyaz.bookifyaz.dto.request.BookingRequest;
import com.bookifyaz.bookifyaz.dto.response.BookingResponse;
import com.bookifyaz.bookifyaz.dto.response.ServiceResponse;
import com.bookifyaz.bookifyaz.dto.response.StaffResponse;
import com.bookifyaz.bookifyaz.repository.StaffRepository;
import com.bookifyaz.bookifyaz.service.StaffService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/public")
@CrossOrigin
public class StaffController {
    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping("/staff")
    public ResponseEntity<List<StaffResponse>> getStaff() {
        return ResponseEntity.status(HttpStatus.CREATED).body(staffService.findStaffByTenant());
    }
}
