package com.bookifyaz.bookifyaz.dto.response;

import java.time.LocalDateTime;

public record BookingResponse(
        int id,
        String clientName,
        String clientPhone,
        String staffName,
        String serviceName,
        LocalDateTime startAt,
        LocalDateTime endAt,
        String status,
        String notes
) {}