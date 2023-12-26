package com.example.MuseumSpringBoot.services;

import com.example.MuseumSpringBoot.model.Museum;
import com.example.MuseumSpringBoot.repositories.MuseumRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MuseumService {

    private final MuseumRepository museumRepository;

    public MuseumService(MuseumRepository museumRepository) {
        this.museumRepository = museumRepository;
    }

    public void addMuseum(Museum museum) {
        museumRepository.save(museum);
    }

    public List<Museum> getAll() {
        return museumRepository.findAll();
    }

    public Optional<Museum> getMuseum(int id) {
        return museumRepository.findById(id);
    }
}
