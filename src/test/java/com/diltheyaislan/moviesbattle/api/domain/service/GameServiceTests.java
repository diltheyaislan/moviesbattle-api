package com.diltheyaislan.moviesbattle.api.domain.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.diltheyaislan.moviesbattle.api.core.exception.BusinessException;
import com.diltheyaislan.moviesbattle.api.core.exception.TechnicalException;
import com.diltheyaislan.moviesbattle.api.core.exception.handler.CommonError;
import com.diltheyaislan.moviesbattle.api.domain.dto.AnswerResultDTO;
import com.diltheyaislan.moviesbattle.api.domain.dto.enums.AnswerResult;
import com.diltheyaislan.moviesbattle.api.domain.entity.Game;
import com.diltheyaislan.moviesbattle.api.domain.entity.GameRound;
import com.diltheyaislan.moviesbattle.api.domain.entity.Movie;
import com.diltheyaislan.moviesbattle.api.domain.entity.User;
import com.diltheyaislan.moviesbattle.api.domain.entity.enums.GameStatus;
import com.diltheyaislan.moviesbattle.api.domain.exception.GameOverException;
import com.diltheyaislan.moviesbattle.api.domain.exception.GameRoundNotCreatedException;
import com.diltheyaislan.moviesbattle.api.domain.exception.UserAlreadyHasGameInProgressException;
import com.diltheyaislan.moviesbattle.api.domain.exception.UserHasNoGameInProgressException;
import com.diltheyaislan.moviesbattle.api.domain.repository.IGameRepository;
import com.diltheyaislan.moviesbattle.api.domain.repository.IGameRoundRepository;
import com.diltheyaislan.moviesbattle.api.domain.repository.IMovieRepository;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class GameServiceTests {

	@Autowired
	private GameService gameService;
	
	@MockBean
	private IGameRepository gameRepository;
	
	@MockBean
	private IMovieRepository movieRepository;
	
	@MockBean
	private IGameRoundRepository gameRoundRepository;

	private Game game;
	private GameRound gameRound;
	private Movie movie1, movie2;
	
	@BeforeEach
    public void init() {
		
		User user = User.builder()
				.id(UUID.fromString("d4f9c7e4-9643-4314-9b41-fe42f24a42ec"))
				.build();
		
		game = new Game();
		game.setId(UUID.fromString("c44152d7-591c-4108-b805-43d45b71e284"));
		game.setStatus(GameStatus.IN_PROGRESS);
		game.setResult(0D);
		game.setWrongAnswerCount(0);
		game.setUser(user);
		game.setCreatedAt(LocalDateTime.of(2022, 3, 29, 10, 30, 45));
		game.setUpdatedAt(LocalDateTime.of(2022, 3, 29, 10, 30, 45));
		
		movie1 = Movie.builder()
				.id("xpto1")
				.title("XPTO 1")
				.rating(1D)
				.numberVotes(2)
				.score(2D)
				.build();
		
		movie2 = Movie.builder()
				.id("xpto2")
				.title("XPTO 2")
				.rating(1D)
				.numberVotes(1)
				.score(1D)
				.build();
					
		gameRound = GameRound.builder()
				.id(UUID.fromString("17bf5840-1878-4fb1-b580-44f49770773e"))
				.game(game)
				.answered(false)
				.userCorrectAnswer(false)
				.movies(Set.of(movie1, movie2))
				.build();
		
		game.setRounds(Set.of(gameRound));
	}
	
	@Test
	public void givenUserId_whenStartGame_shouldSaveAndReturnsCreatedGame() throws BusinessException {
		
		Game expectedGame = game;
	
		when(gameRepository.findOneByUserAndStatus((User) notNull(), (GameStatus) notNull())).thenReturn(Optional.empty());
		when(gameRepository.save((Game) notNull())).thenReturn(expectedGame);
		when(movieRepository.rafflePair()).thenReturn(List.of());
		when(gameRoundRepository.save((GameRound) notNull())).thenReturn(null);
		
		UUID userId = UUID.fromString("d4f9c7e4-9643-4314-9b41-fe42f24a42ec");
		
		Game game = gameService.start(userId);
		
		assertThat(game.getId(), notNullValue());
		assertThat(game.getId().toString(), equalTo(expectedGame.getId().toString()));
		assertThat(game.getStatus().toString(), equalTo(expectedGame.getStatus().toString()));
		assertThat(game.getResult().toString(), equalTo(expectedGame.getResult().toString()));
		assertThat(game.getWrongAnswerCount().toString(), equalTo(expectedGame.getWrongAnswerCount().toString()));
		assertThat(game.getUser().getId().toString(), equalTo(expectedGame.getUser().getId().toString()));
		assertThat(game.getCreatedAt(), notNullValue());
		assertThat(game.getUpdatedAt(), notNullValue());
	}
	
	@Test
	public void givenUserId_whenStartGameWitUserGameAlreadyStarted_shouldThrowsUserAlreadyHasGameInProgressException() throws BusinessException {
		
		Game existingGame = game;
		
		when(gameRepository.findOneByUserAndStatus((User) notNull(), (GameStatus) notNull())).thenReturn(Optional.of(existingGame));
	
		UUID userId = UUID.fromString("d4f9c7e4-9643-4314-9b41-fe42f24a42ec");
		
		UserAlreadyHasGameInProgressException exception = assertThrows(UserAlreadyHasGameInProgressException.class, () -> {
			gameService.start(userId);
		});
		
		String expectedMessage = "message.error.userAlreadyHasGameInProgress";
		String exceptionMessage = exception.getMessage();
		CommonError.Argument expectedExceptionArg = CommonError.Argument.of("game_id", existingGame.getId().toString());
		CommonError.Argument[] exceptionArgs = exception.getArguments();

		assertTrue(exceptionMessage.equalsIgnoreCase(expectedMessage));
		assertTrue(Arrays.asList(exceptionArgs).contains(expectedExceptionArg));
	}
	
	@Test
	public void givenUserId_whenPlayGameWithExistingGameRound_shouldReturnsCurrentGameRound() throws BusinessException, TechnicalException {
		
		GameRound expectedGameRound = gameRound;
	
		when(gameRepository.findOneByUserAndStatus((User) notNull(), (GameStatus) notNull())).thenReturn(Optional.of(game));

		UUID userId = UUID.fromString("d4f9c7e4-9643-4314-9b41-fe42f24a42ec");
		
		GameRound gameRound = gameService.nextRound(userId);
		
		assertThat(gameRound.getId(), notNullValue());
		assertThat(gameRound.getId().toString(), equalTo(expectedGameRound.getId().toString()));
	}
	
	@Test
	public void givenUserId_whenPlayGameWithoutGameRound_shouldCreateGameRoundAndReturnsGameRoundCreated() throws BusinessException, TechnicalException {
		
		List<Movie> expectedMovies = List.of(movie1, movie2);

		GameRound expectedGameRound = gameRound;
		expectedGameRound.setMovies(Set.of(movie1, movie2));
		
		Game expectedGame = game;
		expectedGame.setRounds(new HashSet<GameRound>());
		
		when(gameRepository.findOneByUserAndStatus((User) notNull(), (GameStatus) notNull())).thenReturn(Optional.of(expectedGame));
		when(movieRepository.rafflePair()).thenReturn(expectedMovies);
		when(gameRoundRepository.save((GameRound) notNull())).thenReturn(expectedGameRound);

		UUID userId = UUID.fromString("d4f9c7e4-9643-4314-9b41-fe42f24a42ec");
		
		GameRound gameRound = gameService.nextRound(userId);
	
		assertThat(gameRound.getId(), notNullValue());
		assertThat(gameRound.getId().toString(), equalTo(expectedGameRound.getId().toString()));
		assertThat(gameRound.getMovies().toArray().length, equalTo(expectedMovies.toArray().length));
	}
	
	@Test
	public void givenUserIdAndAnswerMovieId_whenCreateAnswerWithoutACurrentGameInProgress_shouldThrowsUserHasNoGameInProgressException() throws BusinessException {
		
		when(gameRepository.findOneByUserAndStatus((User) notNull(), (GameStatus) notNull())).thenReturn(Optional.empty());
		when(movieRepository.rafflePair()).thenReturn(List.of());
		when(gameRoundRepository.save((GameRound) notNull())).thenReturn(null);
	
		UUID userId = UUID.fromString("d4f9c7e4-9643-4314-9b41-fe42f24a42ec");
		String movieIdAnswer = "xpto1";
		
		UserHasNoGameInProgressException exception = assertThrows(UserHasNoGameInProgressException.class, () -> {
			gameService.createAnswer(userId, movieIdAnswer);
		});
		
		String expectedMessage = "message.error.userHasNoGameInProgress";
		String exceptionMessage = exception.getMessage();

		assertTrue(exceptionMessage.equalsIgnoreCase(expectedMessage));
	}

	@Test
	public void givenUserIdAndAnswerMovieId_whenCreateAnswerWithoutGameRoundCreated_shouldThrowsGameRoundNotCreatedException() throws BusinessException {
	
		Game expectedGame = game;
		expectedGame.setRounds(new HashSet<GameRound>());
		
		when(gameRepository.findOneByUserAndStatus((User) notNull(), (GameStatus) notNull())).thenReturn(Optional.of(expectedGame));
		when(movieRepository.rafflePair()).thenReturn(List.of());
		when(gameRoundRepository.save((GameRound) notNull())).thenReturn(null);
	
		UUID userId = UUID.fromString("d4f9c7e4-9643-4314-9b41-fe42f24a42ec");
		String movieIdAnswer = "xpto1";
		
		GameRoundNotCreatedException exception = assertThrows(GameRoundNotCreatedException.class, () -> {
			gameService.createAnswer(userId, movieIdAnswer);
		});
		
		String expectedMessage = "message.error.gameRoundNotCreated";
		String exceptionMessage = exception.getMessage();

		assertTrue(exceptionMessage.equalsIgnoreCase(expectedMessage));
	}

	@Test
	public void givenUserIdAndAnswerMovieId_whenCreateAnswerWithRightAnswer_shouldReturnsExpectedAnswerResult() throws BusinessException, GameOverException {
	
		AnswerResultDTO expectedAnswerResult = AnswerResultDTO.builder()
				.remainingAttempts(3)
				.result(AnswerResult.RIGHT)
				.build();
		
		when(gameRepository.findOneByUserAndStatus((User) notNull(), (GameStatus) notNull())).thenReturn(Optional.of(game));
		when(gameRepository.save((Game) notNull())).thenReturn(game);
		when(gameRoundRepository.save((GameRound) notNull())).thenReturn(gameRound);
	
		UUID userId = UUID.fromString("d4f9c7e4-9643-4314-9b41-fe42f24a42ec");
		String movieIdAnswer = "xpto1";
		
		AnswerResultDTO result = gameService.createAnswer(userId, movieIdAnswer);
		
		assertThat(result.getResult().toString(), equalTo(expectedAnswerResult.getResult().toString()));
		assertThat(result.getRemainingAttempts().toString(), equalTo(expectedAnswerResult.getRemainingAttempts().toString()));
	}

	@Test
	public void givenUserIdAndAnswerMovieId_whenCreateAnswerWithWrongAnswer_shouldReturnsExpectedAnswerResult() throws BusinessException, GameOverException {
	
		AnswerResultDTO expectedAnswerResult = AnswerResultDTO.builder()
				.remainingAttempts(2)
				.result(AnswerResult.WRONG)
				.build();
		
		when(gameRepository.findOneByUserAndStatus((User) notNull(), (GameStatus) notNull())).thenReturn(Optional.of(game));
		when(gameRepository.save((Game) notNull())).thenReturn(game);
		when(gameRoundRepository.save((GameRound) notNull())).thenReturn(gameRound);
	
		UUID userId = UUID.fromString("d4f9c7e4-9643-4314-9b41-fe42f24a42ec");
		String movieIdAnswer = "xpto2";
		
		AnswerResultDTO result = gameService.createAnswer(userId, movieIdAnswer);
		
		assertThat(result.getResult().toString(), equalTo(expectedAnswerResult.getResult().toString()));
		assertThat(result.getRemainingAttempts().toString(), equalTo(expectedAnswerResult.getRemainingAttempts().toString()));
	}
	
	@Test
	public void givenUserIdAndAnswerMovieId_whenCreateAnswerWithWrongAnswerAndFinishAttempts_shouldThrowsGameRoundNotCreatedException() throws BusinessException {
	
		Game expectedGame = game;
		expectedGame.setWrongAnswerCount(2);
		
		when(gameRepository.findOneByUserAndStatus((User) notNull(), (GameStatus) notNull())).thenReturn(Optional.of(expectedGame));
		when(gameRepository.save((Game) notNull())).thenReturn(expectedGame);
	
		UUID userId = UUID.fromString("d4f9c7e4-9643-4314-9b41-fe42f24a42ec");
		String movieIdAnswer = "xpto2";
		
		GameOverException exception = assertThrows(GameOverException.class, () -> {
			gameService.createAnswer(userId, movieIdAnswer);
		});
		
		String expectedMessage = "message.error.gameOver";
		String exceptionMessage = exception.getMessage();

		assertTrue(exceptionMessage.equalsIgnoreCase(expectedMessage));
	}
	
	@Test
	public void givenUserId_whenFinishGameWithoutACurrentGameInProgress_shouldThrowsUserHasNoGameInProgressException() throws BusinessException {
		
		when(gameRepository.findOneByUserAndStatus((User) notNull(), (GameStatus) notNull())).thenReturn(Optional.empty());
		when(movieRepository.rafflePair()).thenReturn(List.of());
		when(gameRoundRepository.save((GameRound) notNull())).thenReturn(null);
	
		UUID userId = UUID.fromString("d4f9c7e4-9643-4314-9b41-fe42f24a42ec");

		UserHasNoGameInProgressException exception = assertThrows(UserHasNoGameInProgressException.class, () -> {
			gameService.finish(userId);
		});
		
		String expectedMessage = "message.error.userHasNoGameInProgress";
		String exceptionMessage = exception.getMessage();

		assertTrue(exceptionMessage.equalsIgnoreCase(expectedMessage));
	}
	
	@Test
	public void givenUserId_whenFinishGame_shouldReturnsExpectedGame() throws BusinessException, GameOverException {
	
		Game expectedGame = game;
		
		when(gameRepository.findOneByUserAndStatus((User) notNull(), (GameStatus) notNull())).thenReturn(Optional.of(expectedGame));
		when(gameRepository.save((Game) notNull())).thenReturn(game);
	
		UUID userId = UUID.fromString("d4f9c7e4-9643-4314-9b41-fe42f24a42ec");
		
		Game gameResult = gameService.finish(userId);
		
		assertThat(gameResult.getResult().toString(), equalTo(expectedGame.getResult().toString()));
	}
}