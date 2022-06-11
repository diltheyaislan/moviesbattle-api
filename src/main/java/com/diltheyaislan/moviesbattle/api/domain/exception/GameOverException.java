package com.diltheyaislan.moviesbattle.api.domain.exception;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GameOverException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private double result;
	private String message;
	
	public GameOverException(double result) {
		this.result = result;
		this.message = "message.error.gameOver";
	}
	
	public Response getResponse() {
		BigDecimal resultFormatted = BigDecimal.valueOf(result).setScale(1, RoundingMode.HALF_UP);
		return Response.builder()
				.message("message.error.gameOver")
				.result(resultFormatted.doubleValue())
				.build();
	}
	
	@Data
	@Builder
	public static class Response {
		private String message;
		private double result;
	}
}
