package com.diltheyaislan.moviesbattle.api.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.diltheyaislan.moviesbattle.api.domain.entity.Game;
import com.diltheyaislan.moviesbattle.api.domain.entity.User;
import com.diltheyaislan.moviesbattle.api.domain.entity.enums.GameStatus;

@Repository
public interface GameRepository extends JpaRepository<Game, UUID>  {

	public Optional<Game> findOneByUserAndStatus(User user, GameStatus status);
}
