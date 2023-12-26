package com.example.MuseumSpringBoot.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Reservation {
    private int reservationId;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startingTime;
    private Integer duration;
    private int museum;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "reservation_id")
    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    @Basic
    @Column(name = "starting_time")
    public LocalDateTime getStartingTime() {
        return startingTime;
    }

    public void setStartingTime(LocalDateTime startingTime) {
        this.startingTime = startingTime;
    }

    @Basic
    @Column(name = "duration")
    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    @Basic
    @Column(name = "museum")
    public int getMuseum() {
        return museum;
    }

    public void setMuseum(int museum) {
        this.museum = museum;
    }

}
