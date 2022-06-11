package com.diltheyaislan.moviesbattle.api.domain.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.diltheyaislan.moviesbattle.api.domain.entity.GameRound;

@Repository
public interface IGameRoundRepository extends JpaRepository<GameRound, UUID>  {

}
