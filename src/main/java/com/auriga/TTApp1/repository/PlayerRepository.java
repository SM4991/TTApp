package com.auriga.TTApp1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.auriga.TTApp1.model.Player;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
	
}