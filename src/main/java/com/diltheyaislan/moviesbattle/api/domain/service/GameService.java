package com.diltheyaislan.moviesbattle.api.domain.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.diltheyaislan.moviesbattle.api.core.exception.BusinessException;
import com.diltheyaislan.moviesbattle.api.domain.entity.Game;
import com.diltheyaislan.moviesbattle.api.domain.entity.User;
import com.diltheyaislan.moviesbattle.api.domain.entity.enums.GameStatus;
import com.diltheyaislan.moviesbattle.api.domain.entity.factory.GameFactory;
import com.diltheyaislan.moviesbattle.api.domain.entity.factory.UserFactory;
import com.diltheyaislan.moviesbattle.api.domain.exception.UserAlreadyHasGameInProgressException;
import com.diltheyaislan.moviesbattle.api.domain.repository.GameRepository;

@Service
public class GameService {
	
	@Autowired
	private GameRepository gameRepository;
	
	public Game start(UUID userId) throws BusinessException {
		
		User user = UserFactory.create(userId);
		validateExistsGameInProgress(user);
		Game game = GameFactory.create(user, GameStatus.IN_PROGRESS);
		return gameRepository.save(game);
	}
	
	private void validateExistsGameInProgress(User user) throws UserAlreadyHasGameInProgressException {

		Optional<Game> optionalGame = gameRepository.findOneByUserAndStatus(user, GameStatus.IN_PROGRESS);
		if (!optionalGame.isEmpty()) {
			throw new UserAlreadyHasGameInProgressException(optionalGame.get().getId());
		}	
	}
}
