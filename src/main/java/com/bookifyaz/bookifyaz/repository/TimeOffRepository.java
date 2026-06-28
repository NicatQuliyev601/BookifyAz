package com.bookifyaz.bookifyaz.repository;

import com.bookifyaz.bookifyaz.entity.TimeOff;
import com.bookifyaz.bookifyaz.entity.WorkingHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface TimeOffRepository extends JpaRepository<TimeOff, Integer> {

    @Query("SELECT COUNT(t) > 0 FROM TimeOff t " +
            "WHERE t.staff.id = :staffId " +
            "AND :targetDate BETWEEN t.startDate AND t.endDate")
    boolean isStaffOnLeave(
            @Param("staffId") int staffId,
            @Param("targetDate") LocalDate targetDate
    );}
