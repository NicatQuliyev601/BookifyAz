package com.bookifyaz.bookifyaz.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class SlotLockService {

    private static final int LOCK_TTL_SECONDS = 300;
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private final StringRedisTemplate redis;

    public SlotLockService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public String lockSlot(int tenantId, int staffId, LocalDate date, LocalTime time) {
        String key = buildKey(tenantId, staffId, date, time);
        String token = UUID.randomUUID().toString();
        Boolean acquired = redis.opsForValue().setIfAbsent(key, token, LOCK_TTL_SECONDS, TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(acquired)) {
            throw new RuntimeException("Slot is already reserved by someone else. Try another slot.");
        }
        return token;
    }

    public void validateAndReleaseLock(int tenantId, int staffId, LocalDate date, LocalTime time, String lockToken) {
        String key = buildKey(tenantId, staffId, date, time);
        String stored = redis.opsForValue().get(key);
        if (stored == null) {
            throw new RuntimeException("Slot reservation expired. Please select the slot again.");
        }
        if (!stored.equals(lockToken)) {
            throw new RuntimeException("Invalid lock token.");
        }
        redis.delete(key);
    }

    public boolean isLocked(int tenantId, int staffId, LocalDate date, LocalTime time) {
        return Boolean.TRUE.equals(redis.hasKey(buildKey(tenantId, staffId, date, time)));
    }

    public int getLockTtlSeconds() {
        return LOCK_TTL_SECONDS;
    }

    private String buildKey(int tenantId, int staffId, LocalDate date, LocalTime time) {
        return String.format("slot:lock:%d:%d:%s:%s", tenantId, staffId, date, time.format(TIME_FMT));
    }
}
