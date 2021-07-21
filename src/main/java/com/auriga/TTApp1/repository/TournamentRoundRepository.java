package com.auriga.TTApp1.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.auriga.TTApp1.model.Tournament;
import com.auriga.TTApp1.model.TournamentRound;
import com.auriga.TTApp1.model.TournamentType;

@Repository
public interface TournamentRoundRepository extends JpaRepository<TournamentRound, Long> {
	long countByTournamentType(TournamentType tournamentType);
	
	List<TournamentRound> findByTournamentType(TournamentType tournamentType);
	
	List<TournamentRound> findByTournamentTypeOrderByOrderAsc(TournamentType tournamentType);
	
	TournamentRound findByTournamentTypeAndOrder(TournamentType tournamentType, Integer order);	
}
