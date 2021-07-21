package com.auriga.TTApp1.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.auriga.TTApp1.model.Tournament;
import com.auriga.TTApp1.model.TournamentMatch;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
	@Query("from Tournament t WHERE DATE(t.startDate) <= DATE(NOW()) and t.status != 'COMPLETE'")
	Page<Tournament> findAllOngoing(Pageable paging);
	
	@Query("from Tournament t WHERE DATE(t.startDate) > DATE(NOW())")
	Page<Tournament> findAllUpcoming(Pageable paging);
	
	@Query("from Tournament t WHERE DATE(t.startDate) <= DATE(NOW()) and t.status = 'COMPLETE'")
	Page<Tournament> findAllPrevious(Pageable paging);
}
