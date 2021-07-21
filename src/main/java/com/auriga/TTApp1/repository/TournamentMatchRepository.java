package com.auriga.TTApp1.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.auriga.TTApp1.constants.MatchStatusEnum;
import com.auriga.TTApp1.constants.RoundTypeEnum;
import com.auriga.TTApp1.model.Tournament;
import com.auriga.TTApp1.model.TournamentMatch;
import com.auriga.TTApp1.model.TournamentRound;
import com.auriga.TTApp1.model.TournamentType;
import com.auriga.TTApp1.model.User;

@Repository
public interface TournamentMatchRepository extends JpaRepository<TournamentMatch, Long> {
	@Query("from TournamentMatch tm INNER JOIN TournamentRound tr on tr.id = tm.tournamentRound WHERE tr.tournamentType=:tournamentType")
	List<TournamentMatch> findAllTournamentMatch(TournamentType tournamentType);
	
	@Query("from TournamentMatch tm INNER JOIN TournamentRound tr on tr.id = tm.tournamentRound WHERE tr.tournamentType=:tournamentType")
	Page<TournamentMatch> findAllTournamentMatch(Pageable paging, TournamentType tournamentType);
	
	@Query("from TournamentMatch tm INNER JOIN TournamentRound tr on tr.id = tm.tournamentRound WHERE tr.tournamentType=:tournamentType AND tm.status != 'INACTIVE'")
	List<TournamentMatch> findAllActiveTournamentMatch(TournamentType tournamentType);

	List<TournamentMatch> findAllByTournamentRound(TournamentRound round);
	
	List<TournamentMatch> findAllByTournamentRoundAndByeGiven(TournamentRound round, Boolean byeGiven);
	
	TournamentMatch findByTournamentRoundAndOrder(TournamentRound round, Integer order);
	
	@Query(nativeQuery = true, value="SELECT tm.tournament_round_id FROM tournament_matches tm INNER JOIN tournament_rounds tr on tr.id = tm.tournament_round_id WHERE tr.tournament_type_id=:tournament_type_id GROUP BY tournament_round_id HAVING (count(tournament_round_id)%2) > 0 LIMIT 1")
	Long findOddMatchRoundId(Long tournament_type_id);
}
