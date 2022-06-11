package com.diltheyaislan.moviesbattle.api.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.diltheyaislan.moviesbattle.api.core.exception.BusinessException;
import com.diltheyaislan.moviesbattle.api.core.exception.TechnicalException;
import com.diltheyaislan.moviesbattle.api.domain.dto.AnswerResultDTO;
import com.diltheyaislan.moviesbattle.api.domain.entity.Game;
import com.diltheyaislan.moviesbattle.api.domain.entity.GameRound;
import com.diltheyaislan.moviesbattle.api.domain.exception.GameOverException;
import com.diltheyaislan.moviesbattle.api.domain.service.GameService;
import com.diltheyaislan.moviesbattle.api.security.UserPrincipal;
import com.diltheyaislan.moviesbattle.api.security.annotation.AuthenticatedUser;
import com.diltheyaislan.moviesbattle.api.web.request.CreateAnswerBodyRequest;
import com.diltheyaislan.moviesbattle.api.web.response.AnswerResultResponse;
import com.diltheyaislan.moviesbattle.api.web.response.GameCardResponse;
import com.diltheyaislan.moviesbattle.api.web.response.GameCreatedResponse;
import com.diltheyaislan.moviesbattle.api.web.response.GameFinishResponse;
import com.diltheyaislan.moviesbattle.api.web.response.MovieOptionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/games")
@AllArgsConstructor
@Tag(name = "${api.service.game.name}", description = "${api.service.game.description}")
public class GameController extends BaseController {
	
	private GameService gameService;

	@Operation(description = "${api.service.game.start.description}")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "${api.response.game.start.201.description}"),
			@ApiResponse(responseCode = "400", description = "${api.response.game.start.400.description}", content = @Content),
			@ApiResponse(responseCode = "401", description = "${api.response.401.description}", content = @Content)})
	@PostMapping
	public ResponseEntity<GameCreatedResponse> createNewGame(
			@Parameter(hidden = true) @AuthenticatedUser UserPrincipal userPrincipal) throws BusinessException, TechnicalException {

		Game game = gameService.start(userPrincipal.getId());
		
		GameCreatedResponse response = new GameCreatedResponse();
        response.setId(game.getId());
        response.setStatus(game.getStatus().toString());
        response.setCreatedAt(game.getCreatedAt());
        response.setUpdatedAt(game.getUpdatedAt());
        
        return buildResponse(response, HttpStatus.CREATED);
	}

	@Operation(description = "${api.service.game.nextRound.description}")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "${api.response.game.nextRound.200.description}"),
			@ApiResponse(responseCode = "401", description = "${api.response.401.description}", content = @Content)})
	@GetMapping("/next-round")
	public ResponseEntity<GameCardResponse> nextRound(
			@Parameter(hidden = true) @AuthenticatedUser UserPrincipal userPrincipal) throws BusinessException, TechnicalException {
		
		GameRound gameRound = gameService.nextRound(userPrincipal.getId());
		
		List<MovieOptionResponse> movieOptions = new ArrayList<>();
		gameRound.getMovies().stream()
			.forEach(movie -> {
				MovieOptionResponse option = MovieOptionResponse.builder()
						.id(movie.getId())
						.title(movie.getTitle())
						.build();
				movieOptions.add(option);
			});
		
		return responseOk( new GameCardResponse(movieOptions));
	}

	@Operation(description = "${api.service.game.answers.description}")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "${api.response.game.answers.200.description}"),
			@ApiResponse(responseCode = "400", description = "${api.response.game.answers.400.description1}", content = @Content),
			@ApiResponse(responseCode = "400", description = "${api.response.game.answers.400.description2}", content = @Content),
			@ApiResponse(responseCode = "401", description = "${api.response.401.description}", content = @Content)})
	@PostMapping("/answers")
	public ResponseEntity<AnswerResultResponse> createAnswer(
			@Parameter(hidden = true) @AuthenticatedUser UserPrincipal userPrincipal,
			@Valid @RequestBody CreateAnswerBodyRequest request) throws BusinessException, TechnicalException, GameOverException {
		
		AnswerResultDTO result = gameService.createAnswer(userPrincipal.getId(), request.getMovieId());
		AnswerResultResponse response = AnswerResultResponse.builder()
				.result(result.getResult().toString())
				.remainingAttempts(result.getRemainingAttempts())
				.build();

		return responseOk(response);
	}
	
	@Operation(description = "${api.service.game.finish.description}")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "${api.response.game.finish.200.description}"),
			@ApiResponse(responseCode = "400", description = "${api.response.game.finish.400.description}", content = @Content),
			@ApiResponse(responseCode = "401", description = "${api.response.401.description}", content = @Content)})
	@PatchMapping("/finish")
	public ResponseEntity<GameFinishResponse> finishGame(
			@Parameter(hidden = true) @AuthenticatedUser UserPrincipal userPrincipal) throws BusinessException, TechnicalException {

		Game game = gameService.finish(userPrincipal.getId());
		
		GameFinishResponse response = GameFinishResponse.builder()
				.id(game.getId())
				.status(game.getStatus().toString())
				.result(game.getResult())
				.wrongAnswer(game.getWrongAnswerCount())
				.createdAt(game.getCreatedAt())
				.updatedAt(game.getUpdatedAt())
				.build();
        
        return buildResponse(response, HttpStatus.OK);
	}
}
