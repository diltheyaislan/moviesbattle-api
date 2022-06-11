package com.diltheyaislan.moviesbattle.api.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.diltheyaislan.moviesbattle.api.domain.entity.UserScore;
import com.diltheyaislan.moviesbattle.api.domain.service.UserScoreService;
import com.diltheyaislan.moviesbattle.api.web.response.UserScoreResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/scoreboard")
@AllArgsConstructor
@Tag(name = "${api.service.scoreboard.name}", description = "${api.service.scoreboard.description}")
public class ScoreboardController extends BaseController {
	
	private UserScoreService userScoreService;

	@Operation(description = "${api.service.scoreboard.top.description}")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "${api.response.scoreboard.top.200.description}"),
			@ApiResponse(responseCode = "401", description = "${api.response.401.description}", content = @Content)})
	@GetMapping
	public ResponseEntity<List<UserScoreResponse>> createNewGame() {

		List<UserScore> scores = userScoreService.scoreboard();
		
		List<UserScoreResponse> scoreboard = new ArrayList<>();
		
		for (int i = 0; i < scores.size(); i++) {
			scoreboard.add(
					UserScoreResponse.builder()
						.position(i + 1)
						.score(scores.get(i).getScore())
						.name(scores.get(i).getUser().getName())
						.build());
		}

        return responseOk(scoreboard);
	}
}
