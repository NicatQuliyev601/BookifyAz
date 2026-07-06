package com.bookifyaz.bookifyaz.repository;

import com.bookifyaz.bookifyaz.entity.Booking;
import com.bookifyaz.bookifyaz.entity.Service;
import com.bookifyaz.bookifyaz.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query("SELECT b FROM Booking b WHERE b.staff.id = :staffId " +
            "AND CAST(b.startAt AS date) = :date " +
            "AND b.status IN :statuses")
    List<Booking> findByStaffIdAndDateAndStatusIn(
            int staffId,
            LocalDate date,
            List<String> statuses
    );

    @Query("SELECT COUNT(b) FROM Booking b " +
            "WHERE b.tenant.id = :tenantId " +
            "AND MONTH(b.createdAt) = MONTH(CURRENT_DATE) " +
            "AND YEAR(b.createdAt) = YEAR(CURRENT_DATE)")
    int countByTenantAndCurrentMonth(@Param("tenantId") int tenantId);


    @Query("SELECT b FROM Booking b WHERE CAST(b.startAt AS date) = :date AND b.status = :status AND b.staff = :staff")
    List<Booking> findByDateAndStatusAndStaff(@Param("date") LocalDate date, @Param("status") String status, @Param("staff") Staff staff);
}
