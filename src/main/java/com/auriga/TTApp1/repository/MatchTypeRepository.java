package com.auriga.TTApp1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.auriga.TTApp1.model.MatchType;

@Repository
public interface MatchTypeRepository extends JpaRepository<MatchType, Long> {
	@Query(value = "select t.id, t.name from match_types t", nativeQuery = true)
	List<MatchType> findMatchTypesForListing();
}
