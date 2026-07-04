package com.bookifyaz.bookifyaz.dto.response;

public record SlotLockResponse(
        String lockToken,
        int expiresInSeconds
) {
}
