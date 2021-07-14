package com.auriga.TTApp1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.auriga.TTApp1.model.TournamentMatch;
import com.auriga.TTApp1.model.TournamentMatchSet;
import com.auriga.TTApp1.model.User;

@Repository
public interface TournamentMatchSetRepository  extends JpaRepository<TournamentMatchSet, Long> {
	List<TournamentMatchSet> findAllByTournamentMatch(TournamentMatch match);
	
	@Query("SELECT winner FROM TournamentMatchSet tms WHERE tms.tournamentMatch = :match GROUP BY winner_id HAVING count(winner_id) > 1")
	User findTournamentMatchWinner(TournamentMatch match);
}
