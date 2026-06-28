package com.bookifyaz.bookifyaz.repository;

import com.bookifyaz.bookifyaz.entity.Authority;
import com.bookifyaz.bookifyaz.entity.UserAuthority;
import com.bookifyaz.bookifyaz.entity.WorkingHours;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkingHoursRepository extends JpaRepository<WorkingHours, Integer> {
    Optional<WorkingHours> findByStaffIdAndDayOfWeek(int staffId,int dayOfWeek);
}
