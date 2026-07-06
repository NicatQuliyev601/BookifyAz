package com.bookifyaz.bookifyaz.controller;

import com.bookifyaz.bookifyaz.dto.request.BookingRequest;
import com.bookifyaz.bookifyaz.dto.request.SlotLockRequest;
import com.bookifyaz.bookifyaz.dto.response.BookingResponse;
import com.bookifyaz.bookifyaz.dto.response.ServiceResponse;
import com.bookifyaz.bookifyaz.dto.response.SlotLockResponse;
import com.bookifyaz.bookifyaz.dto.response.StaffResponse;
import com.bookifyaz.bookifyaz.redis.SlotLockService;
import com.bookifyaz.bookifyaz.service.BookingService;
import com.bookifyaz.bookifyaz.service.StaffService;
import com.bookifyaz.bookifyaz.service.StaffServicesService;
import com.bookifyaz.bookifyaz.tenant.TenantContext;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("api/v1/public")
@CrossOrigin
public class PublicController {

    private final BookingService bookingService;
    private final SlotLockService slotLockService;
    private final StaffServicesService servicesService;
    private final StaffService staffService;

    public PublicController(BookingService bookingService, SlotLockService slotLockService, StaffServicesService servicesService, StaffService staffService) {
        this.bookingService = bookingService;
        this.slotLockService = slotLockService;
        this.servicesService = servicesService;
        this.staffService = staffService;
    }

    @GetMapping("/slots")
    public ResponseEntity<List<LocalTime>> getAvailableSlots(
            @RequestParam int serviceId,
            @RequestParam int staffId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(bookingService.getAvailableSlots(serviceId, staffId, date));
    }

    @PostMapping("/slots/lock")
    public ResponseEntity<SlotLockResponse> lockSlot(@RequestBody SlotLockRequest request) {
        Integer tenantId = TenantContext.getCurrentTenantId();
        String token = slotLockService.lockSlot(tenantId, request.staffId(), request.date(), request.time());
        return ResponseEntity.ok(new SlotLockResponse(token, slotLockService.getLockTtlSeconds()));
    }

    @PostMapping("/bookings")
    public ResponseEntity<BookingResponse> createBooking(@RequestBody BookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(request));
    }


    @GetMapping("/services")
    public ResponseEntity<List<ServiceResponse>> getServices() {
        return ResponseEntity.ok(servicesService.findServicesByTenant());
    }

    @GetMapping("/staff-by-service/{serviceId}")
    public ResponseEntity<List<StaffResponse>> getStaffByService(@PathVariable int serviceId) {
        return ResponseEntity.ok(servicesService.findStaffByServiceId(serviceId));
    }

    @GetMapping("/staff")
    public ResponseEntity<List<StaffResponse>> getStaff() {
        return ResponseEntity.status(HttpStatus.CREATED).body(staffService.findStaffByTenant());
    }
}
