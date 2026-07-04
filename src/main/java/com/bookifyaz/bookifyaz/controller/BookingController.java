package com.bookifyaz.bookifyaz.controller;

import com.bookifyaz.bookifyaz.dto.request.BookingRequest;
import com.bookifyaz.bookifyaz.dto.request.SlotLockRequest;
import com.bookifyaz.bookifyaz.dto.response.BookingResponse;
import com.bookifyaz.bookifyaz.dto.response.SlotLockResponse;
import com.bookifyaz.bookifyaz.redis.SlotLockService;
import com.bookifyaz.bookifyaz.service.BookingService;
import com.bookifyaz.bookifyaz.tenant.TenantContext;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/public")
@CrossOrigin
public class BookingController {

    private final BookingService bookingService;
    private final SlotLockService slotLockService;

    public BookingController(BookingService bookingService, SlotLockService slotLockService) {
        this.bookingService = bookingService;
        this.slotLockService = slotLockService;
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
}
