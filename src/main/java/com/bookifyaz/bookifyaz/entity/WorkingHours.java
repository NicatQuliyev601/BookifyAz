package com.bookifyaz.bookifyaz.entity;

import jakarta.persistence.*;

import java.time.LocalTime;
@Entity
@Table(name = "working_hours")
public class WorkingHours {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private  int dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isOff;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private Staff staff;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public boolean isOff() {
        return isOff;
    }

    public void setOff(boolean off) {
        isOff = off;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }
}
