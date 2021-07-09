package com.auriga.TTApp1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.auriga.TTApp1.model.TournamentMatch;
import com.auriga.TTApp1.model.TournamentMatchSet;

@Repository
public interface TournamentMatchSetRepository  extends JpaRepository<TournamentMatchSet, Long> {
	@Query("from TournamentMatchSet tms where tms.tournamentMatch = :match")
	List<TournamentMatchSet> findAllByTournamentMatch(TournamentMatch match);
	
//	@Query("from TournamentMatchSet tms where tms.tournamentMatch = :match and ")
}
