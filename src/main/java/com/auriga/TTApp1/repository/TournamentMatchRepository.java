package com.auriga.TTApp1.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.auriga.TTApp1.model.MatchType;
import com.auriga.TTApp1.model.Tournament;
import com.auriga.TTApp1.model.TournamentMatch;
import com.auriga.TTApp1.model.TournamentRound;

@Repository
public interface TournamentMatchRepository extends JpaRepository<TournamentMatch, Long> {
	@Query("from TournamentMatch tm INNER JOIN TournamentRound tr WHERE tr.tournament=:tournament AND tr.matchType=:matchType")
	List<TournamentMatch> findAllTournamentMatch(Tournament tournament, MatchType matchType);
	
	@Query("from TournamentMatch tm INNER JOIN TournamentRound tr WHERE tr.tournament=:tournament AND tr.matchType=:matchType")
	Page<TournamentMatch> findAllTournamentMatch(Pageable paging, Tournament tournament, MatchType matchType);
	
	@Query("from TournamentMatch tm WHERE tm.tournamentRound=:round")
	List<TournamentMatch> findAllByTournamentRound(TournamentRound round);
}
