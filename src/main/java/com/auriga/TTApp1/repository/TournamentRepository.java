package com.auriga.TTApp1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.auriga.TTApp1.model.Tournament;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {

}
