package com.diltheyaislan.moviesbattle.api.core.exception.handler;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(Include.NON_NULL)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommonError {

	private Integer status;
	private LocalDateTime dateTime;
	private String message;
	private List<Argument> args;
	
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Argument {
		private String title;
		private String message;
		
		public static Argument of(String title, String message) {
			return new Argument(title, message);
		}
	}
}
