package com.diltheyaislan.moviesbattle.api.domain.exception;

import com.diltheyaislan.moviesbattle.api.core.exception.BusinessException;
import com.diltheyaislan.moviesbattle.api.core.exception.StatusException;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserHasNoGameInProgressException extends BusinessException {

	private static final long serialVersionUID = 1L;
	
	public UserHasNoGameInProgressException() {
		super(StatusException.BAD_REQUEST, "message.error.userHasNoGameInProgress");
	}
}
