package com.auriga.TTApp1.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.auriga.TTApp1.model.Role;
import com.auriga.TTApp1.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
	@Query("from User where email=?1 and is_login_active = 1")
	User findByEmail(String email);
	
	@Query("from User u where u.role=:role and u.email=:email")
	User findPlayerByEmail(@Param("email") String email, @Param("role") Role role);
	
	@Query("from User u where u.role=:role")
	List<User> findAllPlayer(@Param("role") Role role);
	
	@Query("from User u where u.role=:role")
	Page<User> findAllPlayer(Pageable paging, @Param("role") Role role);
	
	@Query("from User u where u.role=:role and u.id=:id")
	Optional<User> findPlayerById(@Param("id") Long id, @Param("role") Role role);
	
	@Query("from User u where u.role=:role and u.id=:id")
	User getPlayerById(@Param("id") Long id, @Param("role") Role role);
}
