package com.bookifyaz.bookifyaz.repository;

import com.bookifyaz.bookifyaz.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
}
