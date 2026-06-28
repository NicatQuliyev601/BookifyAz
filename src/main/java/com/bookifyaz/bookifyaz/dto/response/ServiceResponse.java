package com.bookifyaz.bookifyaz.dto.response;

import java.math.BigDecimal;

public record ServiceResponse(Integer id,
                              String name,
                              int minDuration,
                              BigDecimal price,
                              String color,
                              boolean isActive) {
}
