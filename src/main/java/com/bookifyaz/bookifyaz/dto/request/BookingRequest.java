package com.bookifyaz.bookifyaz.dto.request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record BookingRequest(
        LocalDateTime startAt,
        LocalDateTime endAt,
        String status,
        String notes,
        LocalDate date,
        LocalTime time,
        String phoneNumber,
        String clientName,
        String email,
        int staffId,
        int serviceId) {
}
