package com.example.MuseumSpringBoot.repositories;

import com.example.MuseumSpringBoot.model.Museum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MuseumRepository extends JpaRepository<Museum, Integer> {

}
