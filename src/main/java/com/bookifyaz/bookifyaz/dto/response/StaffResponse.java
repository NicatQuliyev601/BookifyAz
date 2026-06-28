package com.bookifyaz.bookifyaz.dto.response;

public record StaffResponse(
        Integer id,
        String bio,
        String photoUrl,
        boolean isActive
) {
}
