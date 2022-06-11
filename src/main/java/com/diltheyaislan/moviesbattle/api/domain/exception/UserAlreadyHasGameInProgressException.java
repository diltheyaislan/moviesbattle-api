package com.diltheyaislan.moviesbattle.api.domain.exception;

import java.util.UUID;

import com.diltheyaislan.moviesbattle.api.core.exception.BusinessException;
import com.diltheyaislan.moviesbattle.api.core.exception.StatusException;
import com.diltheyaislan.moviesbattle.api.core.exception.handler.CommonError;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserAlreadyHasGameInProgressException extends BusinessException {

	private static final long serialVersionUID = 1L;
	private UUID gameId;
	
	public UserAlreadyHasGameInProgressException(UUID gameId) {
		super(
				StatusException.BAD_REQUEST, 
				"message.error.userAlreadyHasGameInProgress", 
				CommonError.Argument.of("game_id", gameId.toString()));
		this.gameId = gameId;
	}
}
