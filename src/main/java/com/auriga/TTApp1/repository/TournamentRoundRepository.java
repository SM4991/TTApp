package com.auriga.TTApp1.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.auriga.TTApp1.model.MatchType;
import com.auriga.TTApp1.model.Tournament;
import com.auriga.TTApp1.model.TournamentRound;

@Repository
public interface TournamentRoundRepository extends JpaRepository<TournamentRound, Long> {
	
	@Query("SELECT COUNT(tr) from TournamentRound tr where tournament=:tournament and matchType=:matchType")
	long countByTournamentAndMatchType(@Param("tournament") Tournament tournament, @Param("matchType") MatchType matchType);

	@Query("from TournamentRound tr where tournament=:tournament and matchType=:matchType")
	List<TournamentRound> findByTournamentAndMatchType(@Param("tournament") Tournament tournament, @Param("matchType") MatchType matchType);

//	@Query("from TournamentRound tr JOIN FETCH tr.matches where tournament=:tournament and matchType=:matchType")
//	List<TournamentRound> findByTournamentAndMatchTypeAndFetchMatchesEagerly(@Param("tournament") Tournament tournament, @Param("matchType") MatchType matchType);

}
