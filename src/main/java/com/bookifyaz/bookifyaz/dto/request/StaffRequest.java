package com.bookifyaz.bookifyaz.dto.request;

public record StaffRequest(String fullName,
                           String phoneNumber,
                           String email,
                           String bio,
                           String photoUrl,
                           boolean isActive) {
}
