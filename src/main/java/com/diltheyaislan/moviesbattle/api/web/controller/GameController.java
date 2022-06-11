package com.diltheyaislan.moviesbattle.api.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.diltheyaislan.moviesbattle.api.core.exception.BusinessException;
import com.diltheyaislan.moviesbattle.api.core.exception.TechnicalException;
import com.diltheyaislan.moviesbattle.api.domain.entity.Game;
import com.diltheyaislan.moviesbattle.api.domain.service.GameService;
import com.diltheyaislan.moviesbattle.api.security.UserPrincipal;
import com.diltheyaislan.moviesbattle.api.security.annotation.AuthenticatedUser;
import com.diltheyaislan.moviesbattle.api.web.response.GameCreatedResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/game")
@AllArgsConstructor
@Tag(name = "${api.service.game.name}", description = "${api.service.game.description}")
public class GameController extends BaseController {
	
	private GameService gameService;

	@Operation(description = "${api.service.game.start.description}")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "${api.response.game.start.201.description}"),
			@ApiResponse(responseCode = "400", description = "${api.response.400.validation.description}", content = @Content),
			@ApiResponse(responseCode = "401", description = "${api.response.401.description}", content = @Content)})
	@PostMapping("/start")
	public ResponseEntity<GameCreatedResponse> start(
			@AuthenticatedUser UserPrincipal userPrincipal) throws BusinessException, TechnicalException {

		Game game = gameService.start(userPrincipal.getId());
		
		GameCreatedResponse response = new GameCreatedResponse();
        response.setId(game.getId());
        response.setStatus(game.getStatus().toString());
        response.setCreatedAt(game.getCreatedAt());
        response.setUpdatedAt(game.getUpdatedAt());
        
        return buildResponse(response, HttpStatus.CREATED);
	}
	

	@Operation(description = "${api.service.game.play.description}")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "${api.response.game.play.200.description}"),
			@ApiResponse(responseCode = "400", description = "${api.response.400.validation.description}", content = @Content),
			@ApiResponse(responseCode = "401", description = "${api.response.401.description}", content = @Content),
			@ApiResponse(responseCode = "403", description = "${api.response.game.403.description}", content = @Content)})
	@PostMapping("/{gameId}/play")
	public ResponseEntity<GameCreatedResponse> play(
			@AuthenticatedUser UserPrincipal userPrincipal) throws BusinessException, TechnicalException {
		
		Game game = gameService.start(userPrincipal.getId());
		
		GameCreatedResponse response = new GameCreatedResponse();
        response.setId(game.getId());
        response.setStatus(game.getStatus().toString());
        response.setCreatedAt(game.getCreatedAt());
        response.setUpdatedAt(game.getUpdatedAt());
        
        return buildResponse(response, HttpStatus.CREATED);
	}
}
