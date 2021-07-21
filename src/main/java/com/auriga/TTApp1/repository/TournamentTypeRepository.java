package com.auriga.TTApp1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.auriga.TTApp1.model.Tournament;
import com.auriga.TTApp1.model.TournamentType;

@Repository
public interface TournamentTypeRepository extends JpaRepository<TournamentType, Long> {
	List<TournamentType> findByTournament(Tournament tournament);
	
	@Query(nativeQuery=true, value="SELECT * FROM tournament_types tt WHERE tt.id=:id AND tt.tournament_id=:tournament_id")
	TournamentType findByIdAndTournamentId(Long id, Long tournament_id);
}
