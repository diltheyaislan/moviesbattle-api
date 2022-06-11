package com.diltheyaislan.moviesbattle.api.domain.entity.factory;

import com.diltheyaislan.moviesbattle.api.domain.entity.Game;
import com.diltheyaislan.moviesbattle.api.domain.entity.User;
import com.diltheyaislan.moviesbattle.api.domain.entity.enums.GameStatus;

public class GameFactory {

	public static Game create(User user, GameStatus status) {
		return Game.builder()
				.status(status)
				.result(0D)
				.wrongAnswerCount(0)
				.user(user)
				.build();
	}
}
