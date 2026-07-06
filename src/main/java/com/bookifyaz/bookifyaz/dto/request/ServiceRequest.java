package com.bookifyaz.bookifyaz.dto.request;

import java.math.BigDecimal;

public record ServiceRequest(String name,
                             int minDuration,
                             BigDecimal price,
                             String color,
                             boolean isActive) {
}
