package com.diltheyaislan.moviesbattle.api.domain.service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.diltheyaislan.moviesbattle.api.core.exception.BusinessException;
import com.diltheyaislan.moviesbattle.api.core.exception.TechnicalException;
import com.diltheyaislan.moviesbattle.api.domain.dto.AnswerResultDTO;
import com.diltheyaislan.moviesbattle.api.domain.dto.enums.AnswerResult;
import com.diltheyaislan.moviesbattle.api.domain.entity.Game;
import com.diltheyaislan.moviesbattle.api.domain.entity.GameRound;
import com.diltheyaislan.moviesbattle.api.domain.entity.Movie;
import com.diltheyaislan.moviesbattle.api.domain.entity.User;
import com.diltheyaislan.moviesbattle.api.domain.entity.enums.GameStatus;
import com.diltheyaislan.moviesbattle.api.domain.entity.factory.GameFactory;
import com.diltheyaislan.moviesbattle.api.domain.entity.factory.UserFactory;
import com.diltheyaislan.moviesbattle.api.domain.exception.GameOverException;
import com.diltheyaislan.moviesbattle.api.domain.exception.GameRoundNotCreatedException;
import com.diltheyaislan.moviesbattle.api.domain.exception.InvalidMovieIdAnswerException;
import com.diltheyaislan.moviesbattle.api.domain.exception.UserAlreadyHasGameInProgressException;
import com.diltheyaislan.moviesbattle.api.domain.exception.UserHasNoGameInProgressException;
import com.diltheyaislan.moviesbattle.api.domain.repository.IGameRepository;
import com.diltheyaislan.moviesbattle.api.domain.repository.IGameRoundRepository;
import com.diltheyaislan.moviesbattle.api.domain.repository.IMovieRepository;

@Service
public class GameService {
	
	@Autowired
	private IGameRepository gameRepository;
	
	@Autowired
	private IGameRoundRepository gameRoundRepository;
	
	@Autowired
	private IMovieRepository movieRepository;
	
	@Autowired
	private UserScoreService userScoreService;
	
	@Value("${app.domain.game.totalAttempts}")
	private int totalAttempts = 3;
	
	public Game start(UUID userId) throws BusinessException {
		
		User user = UserFactory.create(userId);
		validateExistsGameInProgress(user);
		
		Game game = GameFactory.create(user, GameStatus.IN_PROGRESS);
		game = gameRepository.save(game);
		createRound(game);
		
		return game;
	}

	public GameRound nextRound(UUID userId) throws BusinessException, TechnicalException {
		
		User user = UserFactory.create(userId);
		Optional<Game> optionalGame = getCurrentGame(user);
		Game game = optionalGame.isPresent() ? optionalGame.get() : start(userId);
		
		if (game.getRounds() == null) {
			game.setRounds(Set.of());
		}
		
		Optional<GameRound> optionalGameRound = game.getRounds().stream()
				.filter(round -> (round.getAnswered() == null || !round.getAnswered()))
				.findFirst();
		
		return optionalGameRound.isPresent() ? optionalGameRound.get() : createRound(game);
	}
	
	public AnswerResultDTO createAnswer(UUID userId, String movieIdAnswer) 
			throws UserHasNoGameInProgressException, GameRoundNotCreatedException, InvalidMovieIdAnswerException, GameOverException {
		
		User user = UserFactory.create(userId);
		Game game = getCurrentGame(user).orElseThrow(UserHasNoGameInProgressException::new);
		
		GameRound gameRound = game.getRounds().stream()
				.filter(round -> (round.getAnswered() == null || !round.getAnswered()))
				.findFirst()
				.orElseThrow(GameRoundNotCreatedException::new);
		
		Movie movieAnswer = getMovieById(movieIdAnswer, gameRound.getMovies());
		Movie rightMovieAnswer = getMovieByHighestScore(gameRound.getMovies());
		
		AnswerResult result = movieAnswer.getId().equalsIgnoreCase(rightMovieAnswer.getId())
				? AnswerResult.RIGHT
				: AnswerResult.WRONG;
		
		gameRound.setAnswered(true);
		
		if (result == AnswerResult.RIGHT) {
			gameRound.setUserCorrectAnswer(true);
		} else {
			gameRound.setUserCorrectAnswer(false);
			game.setWrongAnswerCount(game.getWrongAnswerCount() + 1);
		}
		
		gameRoundRepository.save(gameRound);
		
		int remainingAttempts = totalAttempts - game.getWrongAnswerCount();
		
		if (remainingAttempts <= 0) {
			game = finishGame(game);
			throw new GameOverException(game.getResult());
		}
		
		gameRepository.save(game);
		
		return AnswerResultDTO.builder()
				.result(result)
				.remainingAttempts(remainingAttempts)
				.build();
	}
	
	public Game finish(UUID userId) throws UserHasNoGameInProgressException {
		
		User user = UserFactory.create(userId);
		Game game = getCurrentGame(user).orElseThrow(UserHasNoGameInProgressException::new);
		
		return finishGame(game);
	}
	
	
	private Game finishGame(Game game) {
		
		double gameResult = calculateGameResult(game.getRounds());
		game.setResult(gameResult);
		game.setStatus(GameStatus.FINISHED);
		
		userScoreService.incrementScore(game.getUser(), gameResult);
		
		return gameRepository.save(game);
	}
	
	private void validateExistsGameInProgress(User user) throws UserAlreadyHasGameInProgressException {

		Optional<Game> optionalGame = getCurrentGame(user);
		if (!getCurrentGame(user).isEmpty()) {
			throw new UserAlreadyHasGameInProgressException(optionalGame.get().getId());
		}	
	}
	
	private Movie getMovieById(String movieId, Set<Movie> movies) throws InvalidMovieIdAnswerException {

		return movies.stream()
			.filter(movie -> movie.getId().equalsIgnoreCase(movieId))
			.findFirst()
			.orElseThrow(InvalidMovieIdAnswerException::new);
	}
	
	private Movie getMovieByHighestScore(Set<Movie> movies) {
		
		return movies.stream()
				.max(Comparator.comparing(Movie::getScore))
				.orElseThrow(NoSuchElementException::new);
	}

	private double calculateGameResult(Set<GameRound> rounds) {
		
		Integer rightRoundsCount = rounds.stream()
				.filter(round -> round.getAnswered() != null && round.getAnswered())
				.filter(round -> round.getUserCorrectAnswer())
				.toArray()
				.length;
		
		Integer roundsCount = rounds.stream()
				.filter(round -> round.getAnswered() != null && round.getAnswered())
				.collect(Collectors.toList())
				.size();
		
		if (roundsCount == 0) {
			return 0D;
		}
		
		double result = (rightRoundsCount.doubleValue() / roundsCount.doubleValue()) * 100D;
		return Precision.round(result, 2);
	}
	
	private Optional<Game> getCurrentGame(User user) {
		return gameRepository.findOneByUserAndStatus(user, GameStatus.IN_PROGRESS);
	}
	
	private GameRound createRound(Game game) {
		
		List<Movie> raffledMovies = raffleMovies(game.getRounds());
		
		GameRound gameRound = GameRound.builder()
				.answered(false)
				.userCorrectAnswer(false)
				.game(game)
				.movies(new HashSet<>(raffledMovies))
				.build();
		
		return gameRoundRepository.save(gameRound);
	}
	
	private List<Movie> raffleMovies(Set<GameRound> rounds) {
		
		List<Movie> raffledMovies = movieRepository.rafflePair();
		
		if (rounds != null && rounds.size() > 0) {
			boolean isRepeatedPair = rounds
					.stream()
					.filter(round -> pairsMatch(round.getMovies(), raffledMovies))
					.findFirst().isPresent();
			
			if (isRepeatedPair) {
				return raffleMovies(rounds);
			}	
		}
		
		return raffledMovies;
	}
	
	private boolean pairsMatch(Set<Movie> roundMovies, List<Movie> raffledMovies) {	
		
		Set<String> movieIds = roundMovies.stream().map(movie -> movie.getId()).collect(Collectors.toSet());
		Set<String> movieRaffledIds = raffledMovies.stream().map(movie -> movie.getId()).collect(Collectors.toSet());
		return movieIds.equals(movieRaffledIds);
	}
}
