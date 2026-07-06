package com.bookifyaz.bookifyaz.controller;

import com.bookifyaz.bookifyaz.dto.response.BookingResponse;
import com.bookifyaz.bookifyaz.service.BookingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/v1/booking")
@CrossOrigin
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public ResponseEntity<List<BookingResponse>> filterBookings(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("status") String status,
            @RequestParam("staffId") int staffId) {
        return ResponseEntity.ok(bookingService.filterBookings(date, status, staffId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<BookingResponse>> findAllBookings() {
        return new ResponseEntity<>(bookingService.findAllBookings(), HttpStatus.ACCEPTED);
    }
}
