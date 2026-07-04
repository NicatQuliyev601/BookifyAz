package com.bookifyaz.bookifyaz.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

public record SlotLockRequest(
        int serviceId,
        int staffId,
        LocalDate date,
        LocalTime time
) {
}
