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
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.diltheyaislan.moviesbattle.api.core.exception.BusinessException;
import com.diltheyaislan.moviesbattle.api.core.exception.handler.CommonError;
import com.diltheyaislan.moviesbattle.api.domain.entity.Game;
import com.diltheyaislan.moviesbattle.api.domain.entity.User;
import com.diltheyaislan.moviesbattle.api.domain.entity.enums.GameStatus;
import com.diltheyaislan.moviesbattle.api.domain.exception.UserAlreadyHasGameInProgressException;
import com.diltheyaislan.moviesbattle.api.domain.repository.GameRepository;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class GameServiceTests {

	@Autowired
	private GameService gameService;
	
	@MockBean
	private GameRepository gameRepository;

	private Game game;
	
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
	}
	
	@Test
	public void givenUserId_whenStartGame_shouldSaveAndReturnsCreatedGame() throws BusinessException {
		
		Game expectedGame = game;
	
		when(gameRepository.findOneByUserAndStatus((User) notNull(), (GameStatus) notNull())).thenReturn(Optional.empty());
		when(gameRepository.save((Game) notNull())).thenReturn(expectedGame);
		
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
}