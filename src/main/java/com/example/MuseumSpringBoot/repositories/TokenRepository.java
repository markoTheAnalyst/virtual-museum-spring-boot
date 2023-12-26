package com.example.MuseumSpringBoot.repositories;

import com.example.MuseumSpringBoot.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {

    Token findByAdminId(int id);
}
