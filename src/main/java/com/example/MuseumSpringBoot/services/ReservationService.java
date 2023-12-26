package com.example.MuseumSpringBoot.services;

import com.example.MuseumSpringBoot.model.Reservation;
import com.example.MuseumSpringBoot.repositories.ReservationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public void addReservation(Reservation reservation) {
        reservationRepository.save(reservation);
    }

    public List<Reservation> getReservations(int museum) {
        return reservationRepository.findByMuseum(museum);
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> getReservation(int reservationId) {
        return reservationRepository.findById(reservationId);
    }

}
