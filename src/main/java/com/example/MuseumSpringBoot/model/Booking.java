package com.example.MuseumSpringBoot.model;

import javax.persistence.*;

@Entity
public class Booking {
    private int bookingId;
    private int user;
    private int reservation;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "booking_id")
    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    @Basic
    @Column(name = "user")
    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    @Basic
    @Column(name = "reservation")
    public int getReservation() {
        return reservation;
    }

    public void setReservation(int reservation) {
        this.reservation = reservation;
    }


}
