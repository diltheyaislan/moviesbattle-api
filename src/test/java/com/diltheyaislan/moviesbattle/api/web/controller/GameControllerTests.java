package com.diltheyaislan.moviesbattle.api.web.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.diltheyaislan.moviesbattle.api.MoviesbattleApplication;
import com.diltheyaislan.moviesbattle.api.core.exception.BusinessException;
import com.diltheyaislan.moviesbattle.api.core.exception.ResourceNotFoundException;
import com.diltheyaislan.moviesbattle.api.core.exception.handler.CommonError.Argument;
import com.diltheyaislan.moviesbattle.api.core.exception.handler.ResponseCommonError;
import com.diltheyaislan.moviesbattle.api.domain.entity.Game;
import com.diltheyaislan.moviesbattle.api.domain.entity.User;
import com.diltheyaislan.moviesbattle.api.domain.entity.enums.GameStatus;
import com.diltheyaislan.moviesbattle.api.domain.exception.UserAlreadyHasGameInProgressException;
import com.diltheyaislan.moviesbattle.api.domain.repository.GameRepository;
import com.diltheyaislan.moviesbattle.api.domain.service.GameService;
import com.diltheyaislan.moviesbattle.api.security.UserDetailsServiceImpl;
import com.diltheyaislan.moviesbattle.api.security.UserPrincipal;
import com.diltheyaislan.moviesbattle.api.security.jwt.JwtTokenProvider;

@SpringBootTest(
		webEnvironment = WebEnvironment.RANDOM_PORT,
		classes = { 
				MoviesbattleApplication.class })
@ExtendWith(SpringExtension.class)
@AutoConfigureWebTestClient
public class GameControllerTests {

	@Autowired
	private WebTestClient webClient;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@MockBean
    private GameService gameService;

	@MockBean
    private GameRepository gameRepository;
	
	@MockBean 
	private UserPrincipal userPrincipal;
	
	@MockBean
	private UserDetailsServiceImpl userDetailsServiceImpl;
	
	private Game game;
	private User user;
	
	private String accessToken;
	
	@BeforeEach
	public void init() throws ResourceNotFoundException {
		
	    user = User.builder()
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
	
		UserPrincipal userPrincipal = UserPrincipal.builder().id(user.getId()).build();
		when(userDetailsServiceImpl.loadUserById((UUID) notNull())).thenReturn(userPrincipal);
		accessToken = jwtTokenProvider.createToken(userPrincipal.getId());
	}
	
	@Test
    public void givenRequest_whenStartGame_shouldReturnsCreatedGame() throws BusinessException {

		Game expectedGame = game;

		when(gameService.start((UUID) notNull())).thenReturn(game);
		
		webClient
			.post().uri("/game/start")
			.header("Accept-Language", "en")
			.header("Authorization", "Bearer " + accessToken)
			.exchange()
	    	.expectStatus()
	    		.isCreated()
	    	.expectBody()
	    		.jsonPath("id").isNotEmpty()
	    		.jsonPath("id").isEqualTo(expectedGame.getId().toString());
    }
	
	@Test
    public void givenRequest_whenStartGameWitUserGameAlreadyStarted_shouldReturnsBadRequestAndResponseErrorWithInvalidAttributes() throws BusinessException {
    	
		UUID existingGameId = UUID.fromString("c44152d7-591c-4108-b805-43d45b71e284");
		when(gameService.start((UUID) notNull())).thenThrow(new UserAlreadyHasGameInProgressException(existingGameId));
		
		var result 
			= webClient
				.post().uri("/game/start")
				.header("Accept-Language", "en")
				.header("Authorization", "Bearer " + accessToken)
				.exchange()
				.expectStatus()
					.isBadRequest()
	    		.expectBody(ResponseCommonError.class)
	    			.returnResult()
	    			.getResponseBody();
		
		String expectedErrorMessage = "User already has a game in progress";
		Argument expectedGameId = Argument.of("game_id", "c44152d7-591c-4108-b805-43d45b71e284");

		assertTrue(result.getError().getMessage().equalsIgnoreCase(expectedErrorMessage));
		assertTrue(result.getError().getArgs().contains(expectedGameId));
    }
}
