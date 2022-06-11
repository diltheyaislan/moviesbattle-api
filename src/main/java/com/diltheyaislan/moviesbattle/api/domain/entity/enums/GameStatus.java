package com.diltheyaislan.moviesbattle.api.domain.entity.enums;

public enum GameStatus {

	IN_PROGRESS,
	FINISHED;
	
	public static GameStatus getGameStatus(String value) {
		return GameStatus.valueOf(value.toUpperCase());
	}
}
