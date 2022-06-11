package com.diltheyaislan.moviesbattle.api.domain.dto.enums;

public enum AnswerResult {

	RIGHT,
	WRONG;
	
	public static AnswerResult getAnswerResult(String value) {
		return AnswerResult.valueOf(value.toUpperCase());
	}
}
