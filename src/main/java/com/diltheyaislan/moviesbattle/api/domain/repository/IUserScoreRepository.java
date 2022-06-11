package com.diltheyaislan.moviesbattle.api.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.diltheyaislan.moviesbattle.api.domain.entity.User;
import com.diltheyaislan.moviesbattle.api.domain.entity.UserScore;

@Repository
public interface IUserScoreRepository extends JpaRepository<UserScore, UUID>  {

	public Optional<UserScore> findOneByUser(User user);
	
	@Query(value = "SELECT * FROM USER_SCORE ORDER BY SCORE DESC", nativeQuery = true)
	public List<UserScore> topScore(); 
}
